package br.com.ravikyu.barbercontrol.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id,
        UUID clienteId,
        UUID barbeiroId,
        UUID servicoId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String status
) {}
