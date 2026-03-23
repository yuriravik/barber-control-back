package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.dashboard.dto.DashboardResponse;
import br.com.ravikyu.barbercontrol.application.dashboard.service.DashboardService;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.infrastructure.security.CustomUserDetailsService;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtAuthenticationFilter;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.SecurityConfig;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("deveRetornarDashboardComSucesso")
    void deveRetornarDashboardComSucesso() throws Exception {
        var usuario = new UsuarioResponse(UUID.randomUUID(), "admin@email.com", Role.ADMIN, null, null);
        var clientes = Instancio.ofList(ClienteResponse.class).size(2)
                .generate(field(ClienteResponse.class, "email"), gen -> gen.net().email())
                .create();
        var barbeiros = Instancio.ofList(BarbeiroResponse.class).size(1).create();
        var servicos = Instancio.ofList(ServicoResponse.class).size(1).create();
        var agendamentos = Instancio.ofList(AgendamentoResponse.class).size(1).create();

        var dashboardResponse = new DashboardResponse(usuario, clientes, barbeiros, servicos, agendamentos);

        when(dashboardService.obter()).thenReturn(dashboardResponse);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.email").value("admin@email.com"))
                .andExpect(jsonPath("$.usuario.role").value("ADMIN"))
                .andExpect(jsonPath("$.clientes.length()").value(2))
                .andExpect(jsonPath("$.barbeiros.length()").value(1))
                .andExpect(jsonPath("$.servicos.length()").value(1))
                .andExpect(jsonPath("$.agendamentos.length()").value(1));

        verify(dashboardService, times(1)).obter();
    }

    @Test
    @DisplayName("deveRetornarDashboardComListasVazias")
    void deveRetornarDashboardComListasVazias() throws Exception {
        var usuario = new UsuarioResponse(UUID.randomUUID(), "barbeiro@email.com", Role.BARBEIRO, UUID.randomUUID(), UUID.randomUUID());
        var dashboardResponse = new DashboardResponse(usuario, List.of(), List.of(), List.of(), List.of());

        when(dashboardService.obter()).thenReturn(dashboardResponse);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.role").value("BARBEIRO"))
                .andExpect(jsonPath("$.clientes.length()").value(0))
                .andExpect(jsonPath("$.barbeiros.length()").value(0))
                .andExpect(jsonPath("$.servicos.length()").value(0))
                .andExpect(jsonPath("$.agendamentos.length()").value(0));
    }

    @Test
    @DisplayName("deveRetornar403QuandoNaoAutenticado")
    @org.springframework.security.test.context.support.WithAnonymousUser
    void deveRetornar403QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }
}
