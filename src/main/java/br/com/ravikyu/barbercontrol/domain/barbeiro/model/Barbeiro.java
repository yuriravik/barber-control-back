package br.com.ravikyu.barbercontrol.domain.barbeiro.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Barbeiro {

    private UUID id;
    private String nome;
    private String especialidade;
    private BigDecimal percentualComissao;
    private boolean ativo;

    public Barbeiro(UUID id, String nome, String especialidade, BigDecimal percentualComissao, boolean ativo) {
        validarComissao(percentualComissao);
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.percentualComissao = percentualComissao;
        this.ativo = ativo;
    }

    private void validarComissao(BigDecimal percentual) {
        if (percentual.compareTo(BigDecimal.ZERO) < 0 ||
                percentual.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentual inválido");
        }
    }

    public BigDecimal calcularComissao(BigDecimal valorServico) {
        return valorServico.multiply(percentualComissao)
                .divide(new BigDecimal("100"));
    }

    // getters e setId
}