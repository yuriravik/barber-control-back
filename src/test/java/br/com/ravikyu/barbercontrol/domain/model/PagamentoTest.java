package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoTest {

    private Pagamento pagamentoPendente() {
        var agendamentoId = UUID.randomUUID();
        var valor = Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get();
        var forma = Instancio.gen().enumOf(FormaPagamento.class).get();
        return new Pagamento(agendamentoId, valor, forma);
    }

    @Test
    @DisplayName("deveCriarPagamentoComSucesso")
    void deveCriarPagamentoComSucesso() {
        var agendamentoId = UUID.randomUUID();
        var valor = Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get();
        var forma = Instancio.gen().enumOf(FormaPagamento.class).get();

        var pagamento = new Pagamento(agendamentoId, valor, forma);

        assertEquals(agendamentoId, pagamento.getAgendamentoId());
        assertEquals(valor, pagamento.getValor());
        assertEquals(forma, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
        assertNull(pagamento.getPagoEm());
    }

    @Test
    @DisplayName("deveCriarPagamentoComCartao")
    void deveCriarPagamentoComCartao() {
        var pagamento = new Pagamento(UUID.randomUUID(),
                Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get(),
                FormaPagamento.CARTAO);

        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    @DisplayName("deveCriarPagamentoComDinheiro")
    void deveCriarPagamentoComDinheiro() {
        var pagamento = new Pagamento(UUID.randomUUID(),
                Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get(),
                FormaPagamento.DINHEIRO);

        assertEquals(FormaPagamento.DINHEIRO, pagamento.getFormaPagamento());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoValorZero")
    void deveLancarExcecaoQuandoValorZero() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Pagamento(UUID.randomUUID(), BigDecimal.ZERO, FormaPagamento.PIX));

        assertEquals("Valor inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoValorNegativo")
    void deveLancarExcecaoQuandoValorNegativo() {
        var valor = Instancio.gen().math().bigDecimal().max(new BigDecimal("-0.01")).get();

        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Pagamento(UUID.randomUUID(), valor, FormaPagamento.PIX));

        assertEquals("Valor inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveConfirmarPagamento")
    void deveConfirmarPagamento() {
        var pagamento = pagamentoPendente();
        var antes = java.time.LocalDateTime.now().minusSeconds(1);

        pagamento.confirmarPagamento();

        assertEquals(StatusPagamento.PAGO, pagamento.getStatus());
        assertNotNull(pagamento.getPagoEm());
        assertTrue(pagamento.getPagoEm().isAfter(antes));
    }

    @Test
    @DisplayName("deveIniciarComoStatusPendente")
    void deveIniciarComoStatusPendente() {
        var pagamento = pagamentoPendente();

        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    @DisplayName("devePermitirAlterarFormaPagamento")
    void devePermitirAlterarFormaPagamento() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("50.00"), FormaPagamento.PIX);
        var novaForma = Instancio.gen().enumOf(FormaPagamento.class).get();

        pagamento.setFormaPagamento(novaForma);

        assertEquals(novaForma, pagamento.getFormaPagamento());
    }
}
