package br.com.ravikyu.barbercontrol.application.barbeiro.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BarbeiroResponse(
        UUID id,
        String nome,
        String especialidade,
        BigDecimal percentualComissao,
        boolean ativo
) {}
