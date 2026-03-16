package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.RegistrarPagamentoRequest;
import br.com.ravikyu.barbercontrol.application.pagamento.mapper.PagamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoMapperTest {

    private RegistrarPagamentoRequest requestCom(String formaPagamento) {
        return Instancio.of(RegistrarPagamentoRequest.class)
                .generate(field(RegistrarPagamentoRequest.class, "valor"),
                        gen -> gen.math().bigDecimal().scale(2).min(new BigDecimal("0.01")))
                .set(field(RegistrarPagamentoRequest.class, "formaPagamento"), formaPagamento)
                .create();
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainComPix")
    void deveMapearRequestParaDomainComPix() {
        var request = requestCom("PIX");

        var pagamento = PagamentoMapper.toDomain(request);

        assertEquals(request.agendamentoId(), pagamento.getAgendamentoId());
        assertEquals(request.valor(), pagamento.getValor());
        assertEquals(FormaPagamento.PIX, pagamento.getFormaPagamento());
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainComCartao")
    void deveMapearRequestParaDomainComCartao() {
        var pagamento = PagamentoMapper.toDomain(requestCom("CARTAO"));

        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainComDinheiro")
    void deveMapearRequestParaDomainComDinheiro() {
        var pagamento = PagamentoMapper.toDomain(requestCom("DINHEIRO"));

        assertEquals(FormaPagamento.DINHEIRO, pagamento.getFormaPagamento());
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainComFormaPagamentoEmMinusculas")
    void deveMapearRequestParaDomainComFormaPagamentoEmMinusculas() {
        var pagamento = PagamentoMapper.toDomain(requestCom("pix"));

        assertEquals(FormaPagamento.PIX, pagamento.getFormaPagamento());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoFormaPagamentoInvalida")
    void deveLancarExcecaoQuandoFormaPagamentoInvalida() {
        var request = requestCom("BOLETO");

        assertThrows(IllegalArgumentException.class, () -> PagamentoMapper.toDomain(request));
    }

    @Test
    @DisplayName("deveMapearDomainParaResponse")
    void deveMapearDomainParaResponse() {
        var agendamentoId = UUID.randomUUID();
        var valor = Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get();
        var forma = Instancio.gen().enumOf(FormaPagamento.class).get();
        var pagamento = new Pagamento(agendamentoId, valor, forma);
        var id = UUID.randomUUID();
        pagamento.setId(id);

        var response = PagamentoMapper.toResponse(pagamento);

        assertEquals(id, response.id());
        assertEquals(agendamentoId, response.agendamentoId());
        assertEquals(valor, response.valor());
        assertEquals(forma.name(), response.formaPagamento());
        assertEquals("PENDENTE", response.status());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponseComStatusPago")
    void deveMapearDomainParaResponseComStatusPago() {
        var valor = Instancio.gen().math().bigDecimal().scale(2).min(new BigDecimal("0.01")).get();
        var pagamento = new Pagamento(UUID.randomUUID(), valor, FormaPagamento.CARTAO);
        pagamento.confirmarPagamento();

        var response = PagamentoMapper.toResponse(pagamento);

        assertEquals("PAGO", response.status());
    }
}
