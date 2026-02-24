package br.com.ravikyu.barbercontrol.application.pagamento.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoResponse(
        UUID id,
        UUID agendamentoId,
        BigDecimal valor,
        String formaPagamento,
        String status
) {}