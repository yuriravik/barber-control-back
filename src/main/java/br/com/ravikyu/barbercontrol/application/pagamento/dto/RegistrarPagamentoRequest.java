package br.com.ravikyu.barbercontrol.application.pagamento.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record RegistrarPagamentoRequest(

        @NotNull
        UUID agendamentoId,

        @NotNull
        BigDecimal valor,

        @NotNull
        String formaPagamento
) {}
