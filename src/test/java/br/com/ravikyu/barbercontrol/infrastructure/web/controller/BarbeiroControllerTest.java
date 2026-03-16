package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.service.BarbeiroService;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BarbeiroController.class)
class BarbeiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BarbeiroService service;

    @Test
    void deveCriarBarbeiroComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var response = new BarbeiroResponse(id, "Carlos", "Corte", new BigDecimal("20.00"), true);

        when(service.criar(any())).thenReturn(response);

        mockMvc.perform(post("/barbeiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Carlos",
                                    "especialidade": "Corte",
                                    "percentualComissao": 20.00,
                                    "ativo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Carlos"))
                .andExpect(jsonPath("$.especialidade").value("Corte"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveListarBarbeirosComSucesso() throws Exception {
        var barbeiros = List.of(
                new BarbeiroResponse(UUID.randomUUID(), "Carlos", "Corte", new BigDecimal("20.00"), true),
                new BarbeiroResponse(UUID.randomUUID(), "Ana", "Barba", new BigDecimal("15.00"), false)
        );

        when(service.listar()).thenReturn(barbeiros);

        mockMvc.perform(get("/barbeiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Carlos"))
                .andExpect(jsonPath("$[1].nome").value("Ana"));

        verify(service, times(1)).listar();
    }

    @Test
    void deveDesativarBarbeiroComSucesso() throws Exception {
        var id = UUID.randomUUID();

        doNothing().when(service).desativar(id);

        mockMvc.perform(patch("/barbeiros/{id}/desativar", id))
                .andExpect(status().isOk());

        verify(service, times(1)).desativar(id);
    }

    @Test
    void deveRetornar404AoDesativarBarbeiroNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Barbeiro não encontrado")).when(service).desativar(id);

        mockMvc.perform(patch("/barbeiros/{id}/desativar", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Barbeiro não encontrado"));
    }
}
