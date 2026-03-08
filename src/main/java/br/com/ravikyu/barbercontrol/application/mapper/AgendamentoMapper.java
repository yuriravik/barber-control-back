package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;

public class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static Agendamento toDomain(CriarAgendamentoRequest request) {
        return new Agendamento(
                null,
                request.clienteId(),
                request.barbeiroId(),
                request.servicoId(),
                request.dataHora(),
                request.dataHoraFim(),
                request.status()
        );
    }
}