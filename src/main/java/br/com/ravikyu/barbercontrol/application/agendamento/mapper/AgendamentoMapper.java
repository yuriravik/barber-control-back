package br.com.ravikyu.barbercontrol.application.agendamento.mapper;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;

public class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static Agendamento toDomain(CriarAgendamentoRequest request) {
        return new Agendamento(
                null,
                request.clienteId(),
                request.barbeiroId(),
                request.servicoId(),
                request.dataHora(),
                null,
                StatusAgendamento.AGENDADO
        );
    }
}
