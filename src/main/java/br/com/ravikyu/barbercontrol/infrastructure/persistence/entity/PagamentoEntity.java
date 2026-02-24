package br.com.ravikyu.barbercontrol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private AgendamentoEntity agendamento;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String formaPagamento;

    @Column(nullable = false)
    private String status;

    private LocalDateTime pagoEm;

    // Getters e Setters
}
