package br.com.ravikyu.barbercontrol.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;


public record CriarAgendamentoRequest(

        @NotNull
        UUID clienteId,

        @NotNull
        UUID barbeiroId,

        @NotNull
        UUID servicoId,

        @NotNull
        LocalDateTime dataHora,

        LocalDateTime dataHoraFim,

        String status

) {}