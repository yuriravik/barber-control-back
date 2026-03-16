package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
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

    @InjectMocks
    private AgendamentoService service;

    private static final LocalDateTime DATA_HORA = LocalDateTime.now().plusDays(1);

    private Servico criarServico(UUID id) {
        return new Servico(id, "Corte Simples", "Desc", new BigDecimal("30.00"), 30, true);
    }

    private Cliente criarCliente(UUID id) {
        return new Cliente(id, "João", "joao@email.com", "11999999999");
    }

    private Barbeiro criarBarbeiro(UUID id) {
        return new Barbeiro(id, "Carlos", "Corte", new BigDecimal("20.00"), true);
    }

    private Agendamento criarAgendamento(UUID id, UUID clienteId, UUID barbeiroId, UUID servicoId) {
        return new Agendamento(id, clienteId, barbeiroId, servicoId,
                DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO");
    }

    @Test
    void deveCriarAgendamentoComSucesso() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var request = new CriarAgendamentoRequest(clienteId, barbeiroId, servicoId, DATA_HORA, null, null);
        var servico = criarServico(servicoId);
        var agendamentoSalvo = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(servico));
        when(repository.salvar(any())).thenReturn(agendamentoSalvo);
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamentoSalvo));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(criarBarbeiro(barbeiroId)));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(servico));

        var response = service.criar(request);

        assertNotNull(response);
        assertEquals(agendamentoId, response.id());
        assertEquals("João", response.cliente());
        assertEquals("Carlos", response.barbeiro());
        assertEquals("Corte Simples", response.servico());
        assertEquals("AGENDADO", response.status());
    }

    @Test
    void deveCalcularDataHoraFimAoCriarAgendamento() {
        var servicoId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();
        var servico = new Servico(servicoId, "Barba", "Desc", new BigDecimal("20.00"), 45, true);

        var request = new CriarAgendamentoRequest(clienteId, barbeiroId, servicoId, DATA_HORA, null, null);
        var agendamentoSalvo = new Agendamento(agendamentoId, clienteId, barbeiroId, servicoId,
                DATA_HORA, DATA_HORA.plusMinutes(45), "AGENDADO");

        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(servico));
        when(repository.salvar(any())).thenReturn(agendamentoSalvo);
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamentoSalvo));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(criarBarbeiro(barbeiroId)));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(servico));

        service.criar(request);

        verify(repository).salvar(argThat(a ->
                DATA_HORA.plusMinutes(45).equals(a.getDataHoraFim()) &&
                        "AGENDADO".equals(a.getStatus())
        ));
    }

    @Test
    void deveLancarExcecaoAoCriarAgendamentoComServicoNaoEncontrado() {
        var servicoId = UUID.randomUUID();
        var request = new CriarAgendamentoRequest(
                UUID.randomUUID(), UUID.randomUUID(), servicoId, DATA_HORA, null, null
        );

        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.criar(request));

        assertEquals("Serviço não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    void deveListarAgendamentosComSucesso() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var agendamento = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.listar()).thenReturn(List.of(agendamento));
        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(criarBarbeiro(barbeiroId)));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(criarServico(servicoId)));

        var response = service.listar();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("João", response.get(0).cliente());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaAgendamentos() {
        when(repository.listar()).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBuscarAgendamentoPorIdComSucesso() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var agendamento = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(criarBarbeiro(barbeiroId)));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(criarServico(servicoId)));

        var response = service.buscar(agendamentoId);

        assertNotNull(response);
        assertEquals(agendamentoId, response.id());
        assertEquals("João", response.cliente());
        assertEquals("Carlos", response.barbeiro());
        assertEquals("Corte Simples", response.servico());
        assertEquals("AGENDADO", response.status());
    }

    @Test
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Agendamento não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoClienteDoAgendamentoNaoEncontrado() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var agendamento = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoBarbeiroDoAgendamentoNaoEncontrado() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var agendamento = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Barbeiro não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoServicoDoAgendamentoNaoEncontrado() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var agendamentoId = UUID.randomUUID();

        var agendamento = criarAgendamento(agendamentoId, clienteId, barbeiroId, servicoId);

        when(repository.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(criarCliente(clienteId)));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(criarBarbeiro(barbeiroId)));
        when(servicoRepository.buscarPorId(servicoId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(agendamentoId));

        assertEquals("Serviço não encontrado", ex.getMessage());
    }

    @Test
    void deveDeletarAgendamentoComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(repository).deletar(id);

        service.deletar(id);

        verify(repository, times(1)).deletar(id);
    }
}
