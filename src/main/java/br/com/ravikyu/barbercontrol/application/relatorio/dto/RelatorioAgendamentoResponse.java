package br.com.ravikyu.barbercontrol.application.relatorio.dto;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;

import java.util.List;
import java.util.Map;

public record RelatorioAgendamentoResponse(
        List<AgendamentoResponse> agendamentos,
        int total,
        Map<String, Long> totalPorStatus
) {}
