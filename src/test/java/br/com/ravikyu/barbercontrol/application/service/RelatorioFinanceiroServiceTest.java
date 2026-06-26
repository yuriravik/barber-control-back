package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioFinanceiroService;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
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
class RelatorioFinanceiroServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private BarbeiroRepository barbeiroRepository;

    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private RelatorioFinanceiroService service;

    @Test
    @DisplayName("deveGerarRelatorioFinanceiroParaAdmin")
    void deveGerarRelatorioFinanceiroParaAdmin() {
        var adminId = UUID.randomUUID();
        var admin = usuario(adminId, "admin@barber.com", Role.ADMIN, null, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamento = pagamentoPago(agendamento.getId(), new BigDecimal("80.00"), FormaPagamento.PIX);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarPorBarbeiroIds(List.of(barbeiroId))).thenReturn(List.of(agendamento));
        when(pagamentoRepository.listarComFiltros(List.of(agendamento.getId()), null, null))
                .thenReturn(List.of(pagamento));

        var response = service.gerar(null, null);

        assertNotNull(response);
        assertEquals(new BigDecimal("80.00"), response.totalRecebido());
        assertEquals(1, response.quantidadePagamentos());
        assertTrue(response.totalPorFormaPagamento().containsKey("PIX"));
        assertEquals(new BigDecimal("80.00"), response.totalPorFormaPagamento().get("PIX"));
        assertEquals(1, response.resumoPorBarbeiro().size());
        assertEquals("Barbeiro Teste", response.resumoPorBarbeiro().get(0).nomeBarbeiro());
    }

    @Test
    @DisplayName("deveGerarRelatorioFinanceiroParaBarbeiro")
    void deveGerarRelatorioFinanceiroParaBarbeiro() {
        var adminId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var barbeiroUsuario = usuario(UUID.randomUUID(), "barbeiro@barber.com", Role.BARBEIRO, adminId, barbeiroId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamento = pagamentoPago(agendamento.getId(), new BigDecimal("60.00"), FormaPagamento.DINHEIRO);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(barbeiroUsuario);
        when(barbeiroRepository.buscarPorId(barbeiroId)).thenReturn(Optional.of(barbeiro));
        when(agendamentoRepository.listarPorBarbeiroIds(List.of(barbeiroId))).thenReturn(List.of(agendamento));
        when(pagamentoRepository.listarComFiltros(List.of(agendamento.getId()), null, null))
                .thenReturn(List.of(pagamento));

        var response = service.gerar(null, null);

        assertNotNull(response);
        assertEquals(new BigDecimal("60.00"), response.totalRecebido());
        assertEquals(1, response.quantidadePagamentos());
    }

    @Test
    @DisplayName("deveRetornarVazioParaBarbeiroSemVinculo")
    void deveRetornarVazioParaBarbeiroSemVinculo() {
        var barbeiroUsuario = usuario(UUID.randomUUID(), "barbeiro@barber.com", Role.BARBEIRO, null, null);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(barbeiroUsuario);

        var response = service.gerar(null, null);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.totalRecebido());
        assertEquals(0L, response.quantidadePagamentos());
        assertTrue(response.resumoPorBarbeiro().isEmpty());
    }

    @Test
    @DisplayName("deveGerarRelatorioFinanceiroParaSecretaria")
    void deveGerarRelatorioFinanceiroParaSecretaria() {
        var adminId = UUID.randomUUID();
        var secretaria = usuario(UUID.randomUUID(), "sec@barber.com", Role.SECRETARIA, adminId, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamento = pagamentoPago(agendamento.getId(), new BigDecimal("100.00"), FormaPagamento.CARTAO);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(secretaria);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarPorBarbeiroIds(List.of(barbeiroId))).thenReturn(List.of(agendamento));
        when(pagamentoRepository.listarComFiltros(List.of(agendamento.getId()), null, null))
                .thenReturn(List.of(pagamento));

        var response = service.gerar(null, null);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.totalRecebido());
        assertTrue(response.totalPorFormaPagamento().containsKey("CARTAO"));
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoDataInicioMaiorQueDataFim")
    void deveLancarExcecaoQuandoDataInicioMaiorQueDataFim() {
        var dataInicio = LocalDate.of(2026, 12, 31);
        var dataFim = LocalDate.of(2026, 1, 1);

        assertThrows(BusinessException.class,
                () -> service.gerar(dataInicio, dataFim));
    }

    @Test
    @DisplayName("deveNaoContabilizarPagamentosPendentes")
    void deveNaoContabilizarPagamentosPendentes() {
        var adminId = UUID.randomUUID();
        var admin = usuario(adminId, "admin@barber.com", Role.ADMIN, null, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamentoPendente = pagamentoPendente(agendamento.getId(), new BigDecimal("80.00"));

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarPorBarbeiroIds(List.of(barbeiroId))).thenReturn(List.of(agendamento));
        when(pagamentoRepository.listarComFiltros(List.of(agendamento.getId()), null, null))
                .thenReturn(List.of(pagamentoPendente));

        var response = service.gerar(null, null);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.totalRecebido());
        assertEquals(1, response.quantidadePagamentos());
    }

    private Usuario usuario(UUID id, String email, Role role, UUID adminId, UUID barbeiroId) {
        var usuario = new Usuario(email, "senha123", role);
        usuario.setId(id);
        usuario.setAdminId(adminId);
        usuario.setBarbeiroId(barbeiroId);
        return usuario;
    }

    private Barbeiro barbeiro(UUID id, UUID usuarioId) {
        var barbeiro = new Barbeiro(id, "Barbeiro Teste", "Corte", new BigDecimal("20"), true);
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
                StatusAgendamento.CONCLUIDO
        );
    }

    private Pagamento pagamentoPago(UUID agendamentoId, BigDecimal valor, FormaPagamento forma) {
        var pagamento = new Pagamento(agendamentoId, valor, forma);
        pagamento.setId(UUID.randomUUID());
        pagamento.confirmarPagamento();
        return pagamento;
    }

    private Pagamento pagamentoPendente(UUID agendamentoId, BigDecimal valor) {
        var pagamento = new Pagamento(agendamentoId, valor, FormaPagamento.PIX);
        pagamento.setId(UUID.randomUUID());
        return pagamento;
    }
}
