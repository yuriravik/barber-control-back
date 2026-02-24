package br.com.ravikyu.barbercontrol.application.agendamento.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id,
        UUID clienteId,
        UUID barbeiroId,
        LocalDateTime dataHora,
        String status
) {}
