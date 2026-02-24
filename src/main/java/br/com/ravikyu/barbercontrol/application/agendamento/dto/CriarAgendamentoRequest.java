package br.com.ravikyu.barbercontrol.application.agendamento.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CriarAgendamentoRequest(

        @NotNull
        UUID clienteId,

        @NotNull
        UUID barbeiroId,

        @NotNull
        LocalDateTime dataHora
) {}