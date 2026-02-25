package br.com.ravikyu.barbercontrol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "barbeiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarbeiroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String especialidade;

    @Column(nullable = false)
    private BigDecimal percentualComissao;

    @Column(nullable = false)
    private boolean ativo;

    // Getters e Setters
}
