package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.AgendamentoException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository repository;
    @Mock
    private BarbeiroRepository barbeiroRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private AgendamentoService service;

    private static final LocalDateTime DATA_HORA = LocalDateTime.now().plusDays(1);

    private Cliente clienteValido() {
        return Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();
    }

    private Barbeiro barbeiroValido() {
        return Instancio.of(Barbeiro.class)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    private Servico servicoValido(int duracaoMinutos) {
        return Instancio.of(Servico.class)
                .set(field(Servico.class, "duracaoMinutos"), duracaoMinutos)
                .create();
    }

    private Agendamento agendamentoSalvo(UUID id, UUID clienteId, UUID barbeiroId, UUID servicoId) {
        return Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "id"), id)
                .set(field(Agendamento.class, "clienteId"), clienteId)
                .set(field(Agendamento.class, "barbeiroId"), barbeiroId)
                .set(field(Agendamento.class, "servicoId"), servicoId)
                .set(field(Agendamento.class, "dataHoraInicio"), DATA_HORA)
                .set(field(Agendamento.class, "dataHoraFim"), DATA_HORA.plusMinutes(30))
                .set(field(Agendamento.class, "status"), StatusAgendamento.AGENDADO)
                .create();
    }

    private Usuario adminValido(UUID adminId) {
        var admin = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.ADMIN)
                .set(field(Usuario.class, "adminId"), null)
                .set(field(Usuario.class, "barbeiroId"), null)
                .create();
        admin.setId(adminId);
        return admin;
    }

    @Test
    @DisplayName("deveCriarAgendamentoComSucesso")
    void deveCriarAgendamentoComSucesso() {
        var cliente = clienteValido();
        var barbeiro = barbeiroValido();
        var servico = servicoValido(30);
        var agendamentoId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, cliente.getId(), barbeiro.getId(), servico.getId());

        var request = new CriarAgendamentoRequest(
                cliente.getId(), barbeiro.getId(), servico.getId(), DATA_HORA);

        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));
        when(repository.salvar(any())).thenReturn(agendamento);
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiro.getId())).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));

        var response = service.criar(request);

        assertNotNull(response);
        assertEquals(agendamentoId, response.id());
        assertEquals(cliente.getNome(), response.cliente());
        assertEquals(barbeiro.getNome(), response.barbeiro());
        assertEquals(servico.getNome(), response.servico());
        assertEquals("AGENDADO", response.status());
    }

    @Test
    @DisplayName("deveCalcularDataHoraFimAoCriarAgendamento")
    void deveCalcularDataHoraFimAoCriarAgendamento() {
        var cliente = clienteValido();
        var barbeiro = barbeiroValido();
        var servico = servicoValido(45);
        var agendamentoId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, cliente.getId(), barbeiro.getId(), servico.getId());

        var request = new CriarAgendamentoRequest(
                cliente.getId(), barbeiro.getId(), servico.getId(), DATA_HORA);

        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));
        when(repository.salvar(any())).thenReturn(agendamento);
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiro.getId())).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));

        service.criar(request);

        verify(repository).salvar(argThat(a ->
                DATA_HORA.plusMinutes(45).equals(a.getDataHoraFim()) &&
                        StatusAgendamento.AGENDADO.equals(a.getStatus())
        ));
    }

    @Test
    @DisplayName("deveLancarExcecaoAoCriarAgendamentoComServicoNaoEncontrado")
    void deveLancarExcecaoAoCriarAgendamentoComServicoNaoEncontrado() {
        var servicoId = UUID.randomUUID();
        var request = new CriarAgendamentoRequest(
                UUID.randomUUID(), UUID.randomUUID(), servicoId, DATA_HORA);

        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.criar(request));

        assertEquals("Serviço não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoAoCriarAgendamentoComConflitoDeHorario")
    void deveLancarExcecaoAoCriarAgendamentoComConflitoDeHorario() {
        var barbeiroId = UUID.randomUUID();
        var servico = servicoValido(30);
        servico.setId(UUID.randomUUID());
        var request = new CriarAgendamentoRequest(
                UUID.randomUUID(), barbeiroId, servico.getId(), DATA_HORA);

        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));
        when(repository.existeConflitoHorario(barbeiroId, DATA_HORA, DATA_HORA.plusMinutes(30)))
                .thenReturn(true);

        var ex = assertThrows(AgendamentoException.class, () -> service.criar(request));

        assertEquals("Já existe um agendamento neste horário para o barbeiro informado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveListarAgendamentosDoAdminComSucesso")
    void deveListarAgendamentosDoAdminComSucesso() {
        var adminId = UUID.randomUUID();
        var admin = adminValido(adminId);
        var barbeiro = barbeiroValido();
        barbeiro.setUsuarioId(adminId);
        var cliente = clienteValido();
        var servico = servicoValido(30);
        var agendamentoId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, cliente.getId(), barbeiro.getId(), servico.getId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(repository.listarPorBarbeiroIds(List.of(barbeiro.getId()))).thenReturn(List.of(agendamento));
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiro.getId())).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));

        var response = service.listar();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(cliente.getNome(), response.get(0).cliente());
    }

    @Test
    @DisplayName("deveRetornarListaVaziaParaAdminSemBarbeiros")
    void deveRetornarListaVaziaParaAdminSemBarbeiros() {
        var adminId = UUID.randomUUID();
        var admin = adminValido(adminId);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarAgendamentoPorIdComSucesso")
    void deveBuscarAgendamentoPorIdComSucesso() {
        var cliente = clienteValido();
        var barbeiro = barbeiroValido();
        var servico = servicoValido(30);
        var agendamentoId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, cliente.getId(), barbeiro.getId(), servico.getId());

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiro.getId())).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));

        var response = service.buscar(agendamentoId);

        assertNotNull(response);
        assertEquals(agendamentoId, response.id());
        assertEquals(cliente.getNome(), response.cliente());
        assertEquals(barbeiro.getNome(), response.barbeiro());
        assertEquals(servico.getNome(), response.servico());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoAgendamentoNaoEncontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Agendamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoClienteDoAgendamentoNaoEncontrado")
    void deveLancarExcecaoQuandoClienteDoAgendamentoNaoEncontrado() {
        var agendamentoId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, clienteId, UUID.randomUUID(), UUID.randomUUID());

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoBarbeiroDoAgendamentoNaoEncontrado")
    void deveLancarExcecaoQuandoBarbeiroDoAgendamentoNaoEncontrado() {
        var agendamentoId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, clienteId, barbeiroId, UUID.randomUUID());

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(clienteValido()));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Barbeiro não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoServicoDoAgendamentoNaoEncontrado")
    void deveLancarExcecaoQuandoServicoDoAgendamentoNaoEncontrado() {
        var agendamentoId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamento = agendamentoSalvo(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(clienteValido()));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(barbeiroValido()));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Serviço não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveDeletarAgendamentoComSucesso")
    void deveDeletarAgendamentoComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(repository).deletar(id);

        service.deletar(id);

        verify(repository, times(1)).deletar(id);
    }
}
