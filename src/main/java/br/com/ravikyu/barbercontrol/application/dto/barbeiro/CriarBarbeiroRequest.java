package br.com.ravikyu.barbercontrol.application.dto.barbeiro;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CriarBarbeiroRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String especialidade,

        BigDecimal percentualComissao,

        boolean ativo
) {}