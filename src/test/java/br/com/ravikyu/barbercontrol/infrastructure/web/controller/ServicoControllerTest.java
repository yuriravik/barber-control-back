package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.service.ServicoService;
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

import br.com.ravikyu.barbercontrol.domain.model.Servico;

@WebMvcTest(ServicoController.class)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicoService service;

    @Test
    void deveCriarServicoComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var servico = new Servico(id, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);

        when(service.criar(any())).thenReturn(servico);

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Corte Simples",
                                    "descricao": "Corte básico",
                                    "preco": 30.00,
                                    "duracaoMinutos": 30,
                                    "ativo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Corte Simples"))
                .andExpect(jsonPath("$.preco").value(30.00))
                .andExpect(jsonPath("$.duracaoMinutos").value(30))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveListarServicosComSucesso() throws Exception {
        var servicos = List.of(
                new Servico(UUID.randomUUID(), "Corte", "Desc1", new BigDecimal("30.00"), 30, true),
                new Servico(UUID.randomUUID(), "Barba", "Desc2", new BigDecimal("20.00"), 20, true)
        );

        when(service.listar()).thenReturn(servicos);

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Corte"))
                .andExpect(jsonPath("$[1].nome").value("Barba"));

        verify(service, times(1)).listar();
    }

    @Test
    void deveBuscarServicoPorIdComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var servico = new Servico(id, "Coloração", "Tingimento", new BigDecimal("80.00"), 60, true);

        when(service.buscar(id)).thenReturn(servico);

        mockMvc.perform(get("/servicos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Coloração"));

        verify(service, times(1)).buscar(id);
    }

    @Test
    void deveRetornar404QuandoServicoNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Serviço não encontrado"));

        mockMvc.perform(get("/servicos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Serviço não encontrado"));
    }

    @Test
    void deveDeletarServicoComSucesso() throws Exception {
        var id = UUID.randomUUID();

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/servicos/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).deletar(id);
    }
}
