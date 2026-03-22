package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.infrastructure.security.CustomUserDetailsService;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtAuthenticationFilter;
import br.com.ravikyu.barbercontrol.infrastructure.security.SecurityConfig;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AgendamentoService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private AgendamentoResponse responseValido() {
        return Instancio.of(AgendamentoResponse.class)
                .set(field(AgendamentoResponse.class, "status"), "AGENDADO")
                .create();
    }

    @Test
    @DisplayName("deveCriarAgendamentoComSucesso")
    void deveCriarAgendamentoComSucesso() throws Exception {
        var response = responseValido();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var dataHora = LocalDateTime.now().plusDays(1);

        when(service.criar(any())).thenReturn(response);

        mockMvc.perform(post("/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "clienteId": "%s",
                                    "barbeiroId": "%s",
                                    "servicoId": "%s",
                                    "dataHora": "%s"
                                }
                                """, clienteId, barbeiroId, servicoId, dataHora)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.status").value("AGENDADO"));

        verify(service, times(1)).criar(any());
    }

    @Test
    @DisplayName("deveListarAgendamentosComSucesso")
    void deveListarAgendamentosComSucesso() throws Exception {
        var agendamentos = Instancio.ofList(AgendamentoResponse.class)
                .size(2)
                .set(field(AgendamentoResponse.class, "status"), "AGENDADO")
                .create();

        when(service.listar()).thenReturn(agendamentos);

        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("deveBuscarAgendamentoPorIdComSucesso")
    void deveBuscarAgendamentoPorIdComSucesso() throws Exception {
        var response = responseValido();

        when(service.buscar(response.id())).thenReturn(response);

        mockMvc.perform(get("/agendamentos/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.status").value("AGENDADO"));

        verify(service, times(1)).buscar(response.id());
    }

    @Test
    @DisplayName("deveRetornar404QuandoAgendamentoNaoEncontrado")
    void deveRetornar404QuandoAgendamentoNaoEncontrado() throws Exception {
        var id = Instancio.create(UUID.class);

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Agendamento não encontrado"));

        mockMvc.perform(get("/agendamentos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Agendamento não encontrado"));
    }

    @Test
    @DisplayName("deveDeletarAgendamentoComSucesso")
    void deveDeletarAgendamentoComSucesso() throws Exception {
        var id = Instancio.create(UUID.class);

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/agendamentos/{id}", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(id);
    }
}
