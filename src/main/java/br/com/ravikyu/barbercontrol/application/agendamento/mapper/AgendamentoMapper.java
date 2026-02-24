package br.com.ravikyu.barbercontrol.application.agendamento.mapper;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.*;
import br.com.ravikyu.barbercontrol.domain.agendamento.model.Agendamento;

public class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static Agendamento toDomain(CriarAgendamentoRequest request) {
        return new Agendamento(
                request.clienteId(),
                request.barbeiroId(),
                request.dataHora()
        );
    }

    public static AgendamentoResponse toResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getClienteId(),
                agendamento.getBarbeiroId(),
                agendamento.getDataHora(),
                agendamento.getStatus().name()
        );
    }
}