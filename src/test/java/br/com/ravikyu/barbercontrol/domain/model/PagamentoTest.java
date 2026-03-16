package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoTest {

    @Test
    void deveCriarPagamentoComSucesso() {
        var agendamentoId = UUID.randomUUID();
        var pagamento = new Pagamento(agendamentoId, new BigDecimal("50.00"), FormaPagamento.PIX);

        assertEquals(agendamentoId, pagamento.getAgendamentoId());
        assertEquals(new BigDecimal("50.00"), pagamento.getValor());
        assertEquals(FormaPagamento.PIX, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
        assertNull(pagamento.getPagoEm());
    }

    @Test
    void deveCriarPagamentoComCartao() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("100.00"), FormaPagamento.CARTAO);

        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    void deveCriarPagamentoComDinheiro() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("30.00"), FormaPagamento.DINHEIRO);

        assertEquals(FormaPagamento.DINHEIRO, pagamento.getFormaPagamento());
    }

    @Test
    void deveLancarExcecaoQuandoValorZero() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Pagamento(UUID.randomUUID(), BigDecimal.ZERO, FormaPagamento.PIX));

        assertEquals("Valor inválido", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoValorNegativo() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Pagamento(UUID.randomUUID(), new BigDecimal("-10.00"), FormaPagamento.PIX));

        assertEquals("Valor inválido", ex.getMessage());
    }

    @Test
    void deveConfirmarPagamento() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("75.00"), FormaPagamento.PIX);
        var antes = java.time.LocalDateTime.now().minusSeconds(1);

        pagamento.confirmarPagamento();

        assertEquals(StatusPagamento.PAGO, pagamento.getStatus());
        assertNotNull(pagamento.getPagoEm());
        assertTrue(pagamento.getPagoEm().isAfter(antes));
    }

    @Test
    void deveIniciarComoStatusPendente() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("20.00"), FormaPagamento.DINHEIRO);

        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    void devePermitirAlterarFormaPagamento() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("50.00"), FormaPagamento.PIX);
        pagamento.setFormaPagamento(FormaPagamento.CARTAO);

        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
    }
}
