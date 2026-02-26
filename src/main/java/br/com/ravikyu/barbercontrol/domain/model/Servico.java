package br.com.ravikyu.barbercontrol.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Servico {

    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracaoMinutos;
    private boolean ativo;

    public Servico(UUID id, String nome, String descricao,
                   BigDecimal preco, Integer duracaoMinutos, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
        this.ativo = ativo;
    }
}