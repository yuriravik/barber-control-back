package br.com.ravikyu.barbercontrol.application.agendamento.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id,
        String cliente,
        String barbeiro,
        String servico,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String status
) {}
