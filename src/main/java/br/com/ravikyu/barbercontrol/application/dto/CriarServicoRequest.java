package br.com.ravikyu.barbercontrol.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarServicoRequest (
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    String descricao,

    BigDecimal preco,

    Integer duracaoMinutos,

    boolean ativo
) {}
