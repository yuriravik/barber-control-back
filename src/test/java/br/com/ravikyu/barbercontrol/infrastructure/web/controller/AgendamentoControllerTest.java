package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AgendamentoService service;

    private final LocalDateTime DATA_HORA = LocalDateTime.now().plusDays(1);

    @Test
    void deveCriarAgendamentoComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();

        var response = new AgendamentoResponse(id, "João", "Carlos", "Corte Simples",
                DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO");

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
                                """, clienteId, barbeiroId, servicoId, DATA_HORA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.cliente").value("João"))
                .andExpect(jsonPath("$.barbeiro").value("Carlos"))
                .andExpect(jsonPath("$.servico").value("Corte Simples"))
                .andExpect(jsonPath("$.status").value("AGENDADO"));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveListarAgendamentosComSucesso() throws Exception {
        var agendamentos = List.of(
                new AgendamentoResponse(UUID.randomUUID(), "João", "Carlos", "Corte",
                        DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO"),
                new AgendamentoResponse(UUID.randomUUID(), "Maria", "Ana", "Barba",
                        DATA_HORA.plusHours(2), DATA_HORA.plusHours(2).plusMinutes(20), "AGENDADO")
        );

        when(service.listar()).thenReturn(agendamentos);

        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cliente").value("João"))
                .andExpect(jsonPath("$[1].cliente").value("Maria"));

        verify(service, times(1)).listar();
    }

    @Test
    void deveBuscarAgendamentoPorIdComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var response = new AgendamentoResponse(id, "Pedro", "José", "Coloração",
                DATA_HORA, DATA_HORA.plusMinutes(60), "AGENDADO");

        when(service.buscar(id)).thenReturn(response);

        mockMvc.perform(get("/agendamentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.cliente").value("Pedro"))
                .andExpect(jsonPath("$.barbeiro").value("José"))
                .andExpect(jsonPath("$.servico").value("Coloração"));

        verify(service, times(1)).buscar(id);
    }

    @Test
    void deveRetornar404QuandoAgendamentoNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Agendamento não encontrado"));

        mockMvc.perform(get("/agendamentos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Agendamento não encontrado"));
    }

    @Test
    void deveDeletarAgendamentoComSucesso() throws Exception {
        var id = UUID.randomUUID();

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/agendamentos/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).deletar(id);
    }
}
