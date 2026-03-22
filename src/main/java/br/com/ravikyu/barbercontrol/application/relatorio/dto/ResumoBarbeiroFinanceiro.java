package br.com.ravikyu.barbercontrol.application.relatorio.dto;

import java.math.BigDecimal;

public record ResumoBarbeiroFinanceiro(
        String nomeBarbeiro,
        BigDecimal totalRecebido,
        BigDecimal comissao
) {}
