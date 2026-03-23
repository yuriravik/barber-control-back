package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.application.dashboard.service.DashboardService;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.service.ServicoService;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private BarbeiroService barbeiroService;

    @Mock
    private ServicoService servicoService;

    @Mock
    private AgendamentoService agendamentoService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("deveRetornarDashboardComTodosOsDados")
    void deveRetornarDashboardComTodosOsDados() {
        var usuario = Instancio.of(UsuarioResponse.class)
                .generate(field(UsuarioResponse.class, "email"), gen -> gen.net().email())
                .create();
        var clientes = Instancio.ofList(ClienteResponse.class).size(2)
                .generate(field(ClienteResponse.class, "email"), gen -> gen.net().email())
                .create();
        var barbeiros = Instancio.ofList(BarbeiroResponse.class).size(1).create();
        var servicos = Instancio.ofList(ServicoResponse.class).size(3).create();
        var agendamentos = Instancio.ofList(AgendamentoResponse.class).size(2).create();

        when(usuarioService.buscarAutenticado()).thenReturn(usuario);
        when(clienteService.listar()).thenReturn(clientes);
        when(barbeiroService.listar()).thenReturn(barbeiros);
        when(servicoService.listar()).thenReturn(servicos);
        when(agendamentoService.listar()).thenReturn(agendamentos);

        var response = dashboardService.obter();

        assertNotNull(response);
        assertEquals(usuario, response.usuario());
        assertEquals(2, response.clientes().size());
        assertEquals(1, response.barbeiros().size());
        assertEquals(3, response.servicos().size());
        assertEquals(2, response.agendamentos().size());

        verify(usuarioService, times(1)).buscarAutenticado();
        verify(clienteService, times(1)).listar();
        verify(barbeiroService, times(1)).listar();
        verify(servicoService, times(1)).listar();
        verify(agendamentoService, times(1)).listar();
    }

    @Test
    @DisplayName("deveRetornarDashboardComListasVazias")
    void deveRetornarDashboardComListasVazias() {
        var usuario = Instancio.of(UsuarioResponse.class)
                .generate(field(UsuarioResponse.class, "email"), gen -> gen.net().email())
                .create();

        when(usuarioService.buscarAutenticado()).thenReturn(usuario);
        when(clienteService.listar()).thenReturn(List.of());
        when(barbeiroService.listar()).thenReturn(List.of());
        when(servicoService.listar()).thenReturn(List.of());
        when(agendamentoService.listar()).thenReturn(List.of());

        var response = dashboardService.obter();

        assertNotNull(response);
        assertEquals(usuario, response.usuario());
        assertTrue(response.clientes().isEmpty());
        assertTrue(response.barbeiros().isEmpty());
        assertTrue(response.servicos().isEmpty());
        assertTrue(response.agendamentos().isEmpty());
    }
}
