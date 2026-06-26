package br.com.ravikyu.barbercontrol.application.agendamento.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AtualizarAgendamentoRequest(
        @NotNull(message = "Cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "Barbeiro é obrigatório")
        UUID barbeiroId,

        @NotNull(message = "Serviço é obrigatório")
        UUID servicoId,

        @NotNull(message = "Data e hora são obrigatórias")
        LocalDateTime dataHora
) {}
