package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Pagamento {

    private UUID id;
    private UUID agendamentoId;
    private BigDecimal valor;
    private FormaPagamento formaPagamento;
    private StatusPagamento status;
    private LocalDateTime pagoEm;

    public Pagamento(UUID agendamentoId,
                     BigDecimal valor,
                     FormaPagamento formaPagamento) {

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }

        this.agendamentoId = agendamentoId;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.status = StatusPagamento.PENDENTE;
    }

    public void confirmarPagamento() {
        this.status = StatusPagamento.PAGO;
        this.pagoEm = LocalDateTime.now();
    }

    // getters e setId
}