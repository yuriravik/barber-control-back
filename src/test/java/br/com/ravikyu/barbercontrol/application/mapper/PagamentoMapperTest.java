package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.RegistrarPagamentoRequest;
import br.com.ravikyu.barbercontrol.application.pagamento.mapper.PagamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoMapperTest {

    @Test
    void deveMapearRequestParaDomainComPix() {
        var agendamentoId = UUID.randomUUID();
        var request = new RegistrarPagamentoRequest(agendamentoId, new BigDecimal("50.00"), "PIX");

        var pagamento = PagamentoMapper.toDomain(request);

        assertEquals(agendamentoId, pagamento.getAgendamentoId());
        assertEquals(new BigDecimal("50.00"), pagamento.getValor());
        assertEquals(FormaPagamento.PIX, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    void deveMapearRequestParaDomainComCartao() {
        var request = new RegistrarPagamentoRequest(UUID.randomUUID(), new BigDecimal("100.00"), "CARTAO");

        var pagamento = PagamentoMapper.toDomain(request);

        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
    }

    @Test
    void deveMapearRequestParaDomainComDinheiro() {
        var request = new RegistrarPagamentoRequest(UUID.randomUUID(), new BigDecimal("30.00"), "DINHEIRO");

        var pagamento = PagamentoMapper.toDomain(request);

        assertEquals(FormaPagamento.DINHEIRO, pagamento.getFormaPagamento());
    }

    @Test
    void deveMapearRequestParaDomainComFormaPagamentoEmMinusculas() {
        var request = new RegistrarPagamentoRequest(UUID.randomUUID(), new BigDecimal("40.00"), "pix");

        var pagamento = PagamentoMapper.toDomain(request);

        assertEquals(FormaPagamento.PIX, pagamento.getFormaPagamento());
    }

    @Test
    void deveLancarExcecaoQuandoFormaPagamentoInvalida() {
        var request = new RegistrarPagamentoRequest(UUID.randomUUID(), new BigDecimal("50.00"), "BOLETO");

        assertThrows(IllegalArgumentException.class, () -> PagamentoMapper.toDomain(request));
    }

    @Test
    void deveMapearDomainParaResponse() {
        var agendamentoId = UUID.randomUUID();
        var pagamento = new Pagamento(agendamentoId, new BigDecimal("75.00"), FormaPagamento.PIX);
        var id = UUID.randomUUID();
        pagamento.setId(id);

        var response = PagamentoMapper.toResponse(pagamento);

        assertEquals(id, response.id());
        assertEquals(agendamentoId, response.agendamentoId());
        assertEquals(new BigDecimal("75.00"), response.valor());
        assertEquals("PIX", response.formaPagamento());
        assertEquals("PENDENTE", response.status());
    }

    @Test
    void deveMapearDomainParaResponseComStatusPago() {
        var pagamento = new Pagamento(UUID.randomUUID(), new BigDecimal("50.00"), FormaPagamento.CARTAO);
        pagamento.confirmarPagamento();

        var response = PagamentoMapper.toResponse(pagamento);

        assertEquals("PAGO", response.status());
    }
}
