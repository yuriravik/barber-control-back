package br.com.ravikyu.barbercontrol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "agendamento_id", nullable = false, unique = true)
    private UUID agendamentoId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento;

    @Column(nullable = false)
    private String status;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;
}
