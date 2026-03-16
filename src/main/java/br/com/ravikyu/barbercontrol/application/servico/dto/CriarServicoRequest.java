package br.com.ravikyu.barbercontrol.application.servico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarServicoRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Preço é obrigatório")
        BigDecimal preco,

        @NotNull(message = "Duração é obrigatória")
        Integer duracaoMinutos

) {}
