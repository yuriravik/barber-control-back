package br.com.ravikyu.barbercontrol.application.pagamento.mapper;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.*;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;

public class PagamentoMapper {

    private PagamentoMapper() {}

    public static Pagamento toDomain(RegistrarPagamentoRequest request) {

        FormaPagamento forma = FormaPagamento.valueOf(
                request.formaPagamento().toUpperCase()
        );

        return new Pagamento(
                request.agendamentoId(),
                request.valor(),
                forma
        );
    }

    public static PagamentoResponse toResponse(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getAgendamentoId(),
                pagamento.getValor(),
                pagamento.getFormaPagamento().name(),
                pagamento.getStatus().name()
        );
    }
}