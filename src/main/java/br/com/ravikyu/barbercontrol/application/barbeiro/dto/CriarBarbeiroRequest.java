package br.com.ravikyu.barbercontrol.application.barbeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarBarbeiroRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String especialidade,

        @NotNull(message = "Percentual de comissão é obrigatório")
        BigDecimal percentualComissao

) {}
