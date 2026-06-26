package br.com.ravikyu.barbercontrol.application.servico.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarServicoRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Duração é obrigatória")
        @Min(value = 1, message = "Duração deve ser de pelo menos 1 minuto")
        Integer duracaoMinutos

) {}
