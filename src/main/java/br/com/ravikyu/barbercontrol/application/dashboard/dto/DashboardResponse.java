package br.com.ravikyu.barbercontrol.application.dashboard.dto;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;

import java.util.List;

public record DashboardResponse(
        UsuarioResponse usuario,
        List<ClienteResponse> clientes,
        List<BarbeiroResponse> barbeiros,
        List<ServicoResponse> servicos,
        List<AgendamentoResponse> agendamentos
) {}
