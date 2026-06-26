package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioAgendamentoService;
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
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioAgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private BarbeiroRepository barbeiroRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private RelatorioAgendamentoService service;

    @Test
    @DisplayName("deveGerarRelatorioParaAdmin")
    void deveGerarRelatorioParaAdmin() {
        var adminId = UUID.randomUUID();
        var admin = usuario(adminId, "admin@barber.com", Role.ADMIN, null, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var cliente = cliente(agendamento.getClienteId());
        var servico = servico(agendamento.getServicoId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarComFiltros(List.of(barbeiroId), null, null, null, null))
                .thenReturn(List.of(agendamento));
        when(clienteRepository.buscarPorId(agendamento.getClienteId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(agendamento.getServicoId())).thenReturn(Optional.of(servico));

        var response = service.gerar(null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.total());
        assertEquals(1, response.agendamentos().size());
        assertTrue(response.totalPorStatus().containsKey("AGENDADO"));
        assertEquals(1L, response.totalPorStatus().get("AGENDADO"));
    }

    @Test
    @DisplayName("deveGerarRelatorioParaBarbeiro")
    void deveGerarRelatorioParaBarbeiro() {
        var adminId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var barbeiroUsuario = usuario(UUID.randomUUID(), "barbeiro@barber.com", Role.BARBEIRO, adminId, barbeiroId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var cliente = cliente(agendamento.getClienteId());
        var servico = servico(agendamento.getServicoId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(barbeiroUsuario);
        when(agendamentoRepository.listarComFiltros(List.of(barbeiroId), null, null, null, null))
                .thenReturn(List.of(agendamento));
        when(clienteRepository.buscarPorId(agendamento.getClienteId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(agendamento.getServicoId())).thenReturn(Optional.of(servico));

        var response = service.gerar(null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.total());
    }

    @Test
    @DisplayName("deveRetornarVazioParaBarbeiroSemVinculo")
    void deveRetornarVazioParaBarbeiroSemVinculo() {
        var barbeiroUsuario = usuario(UUID.randomUUID(), "barbeiro@barber.com", Role.BARBEIRO, null, null);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(barbeiroUsuario);

        var response = service.gerar(null, null, null, null);

        assertNotNull(response);
        assertEquals(0, response.total());
        assertTrue(response.agendamentos().isEmpty());
    }

    @Test
    @DisplayName("deveGerarRelatorioParaSecretaria")
    void deveGerarRelatorioParaSecretaria() {
        var adminId = UUID.randomUUID();
        var secretaria = usuario(UUID.randomUUID(), "sec@barber.com", Role.SECRETARIA, adminId, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var cliente = cliente(agendamento.getClienteId());
        var servico = servico(agendamento.getServicoId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(secretaria);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarComFiltros(List.of(barbeiroId), null, null, null, null))
                .thenReturn(List.of(agendamento));
        when(clienteRepository.buscarPorId(agendamento.getClienteId())).thenReturn(Optional.of(cliente));
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(barbeiro));
        when(servicoRepository.buscarPorId(agendamento.getServicoId())).thenReturn(Optional.of(servico));

        var response = service.gerar(null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.total());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoDataInicioMaiorQueDataFim")
    void deveLancarExcecaoQuandoDataInicioMaiorQueDataFim() {
        var dataInicio = LocalDate.of(2026, 12, 31);
        var dataFim = LocalDate.of(2026, 1, 1);

        assertThrows(BusinessException.class,
                () -> service.gerar(dataInicio, dataFim, null, null));
    }

    @Test
    @DisplayName("deveGerarRelatorioComFiltrosDeDatas")
    void deveGerarRelatorioComFiltrosDeDatas() {
        var adminId = UUID.randomUUID();
        var admin = usuario(adminId, "admin@barber.com", Role.ADMIN, null, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var dataInicio = LocalDate.of(2026, 1, 1);
        var dataFim = LocalDate.of(2026, 12, 31);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarComFiltros(
                List.of(barbeiroId), null, null,
                dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                .thenReturn(List.of());

        var response = service.gerar(dataInicio, dataFim, null, null);

        assertNotNull(response);
        assertEquals(0, response.total());
    }

    private Usuario usuario(UUID id, String email, Role role, UUID adminId, UUID barbeiroId) {
        var usuario = new Usuario(email, "senha123", role);
        usuario.setId(id);
        usuario.setAdminId(adminId);
        usuario.setBarbeiroId(barbeiroId);
        return usuario;
    }

    private Barbeiro barbeiro(UUID id, UUID usuarioId) {
        var barbeiro = new Barbeiro(id, "Barbeiro", "Corte", new BigDecimal("20"), true);
        barbeiro.setUsuarioId(usuarioId);
        return barbeiro;
    }

    private Agendamento agendamento(UUID id, UUID barbeiroId) {
        return new Agendamento(
                id,
                UUID.randomUUID(),
                barbeiroId,
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30),
                StatusAgendamento.AGENDADO
        );
    }

    private Cliente cliente(UUID id) {
        return new Cliente(id, "Cliente Teste", "cliente@email.com", "11999999999");
    }

    private Servico servico(UUID id) {
        var servico = new Servico(id, "Corte", "Corte simples", new BigDecimal("50"), 30, true);
        return servico;
    }
}
