package br.com.ravikyu.barbercontrol.application.barbeiro.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarBarbeiroRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String especialidade,

        @NotNull(message = "Percentual de comissão é obrigatório")
        @DecimalMin(value = "0.00", message = "Percentual de comissão deve ser maior ou igual a 0")
        @DecimalMax(value = "100.00", message = "Percentual de comissão deve ser menor ou igual a 100")
        BigDecimal percentualComissao

) {}
