package br.com.ravikyu.barbercontrol.application.dashboard.service;

import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.application.dashboard.dto.DashboardResponse;
import br.com.ravikyu.barbercontrol.application.servico.service.ServicoService;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final AgendamentoService agendamentoService;

    public DashboardResponse obter() {
        return new DashboardResponse(
                usuarioService.buscarAutenticado(),
                clienteService.listar(),
                barbeiroService.listar(),
                servicoService.listar(),
                agendamentoService.listar()
        );
    }
}
