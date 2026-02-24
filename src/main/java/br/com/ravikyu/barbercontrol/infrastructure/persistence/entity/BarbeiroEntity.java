package br.com.ravikyu.barbercontrol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "barbeiros")
public class BarbeiroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String especialidade;

    @Column(nullable = false)
    private BigDecimal percentualComissao;

    // Getters e Setters
}
