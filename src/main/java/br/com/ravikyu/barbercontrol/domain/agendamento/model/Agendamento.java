package br.com.ravikyu.barbercontrol.domain.agendamento.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Agendamento {

    private UUID id;
    private UUID clienteId;
    private UUID barbeiroId;
    private LocalDateTime dataHora;
    private StatusAgendamento status;

    public Agendamento(UUID clienteId, UUID barbeiroId, LocalDateTime dataHora) {
        validarData(dataHora);
        this.clienteId = clienteId;
        this.barbeiroId = barbeiroId;
        this.dataHora = dataHora;
        this.status = StatusAgendamento.AGENDADO;
    }

    private void validarData(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data inválida");
        }
    }

    public void cancelar() {
        this.status = StatusAgendamento.CANCELADO;
    }

    public void concluir() {
        this.status = StatusAgendamento.CONCLUIDO;
    }

    // getters e setId
}