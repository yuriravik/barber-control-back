package br.com.ravikyu.barbercontrol.domain.model;

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
    private UUID servicoId;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String status;

    public Agendamento(UUID id,
                       UUID clienteId,
                       UUID barbeiroId,
                       UUID servicoId,
                       LocalDateTime dataHoraInicio,
                       LocalDateTime dataHoraFim,
                       String status) {
        this.id = id;
        this.clienteId = clienteId;
        this.barbeiroId = barbeiroId;
        this.servicoId = servicoId;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = status;
    }

    public Agendamento() {}

    // getters e setters
}