package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.service.ServicoService;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.instancio.Select.field;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicoController.class)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServicoService service;

    @Test
    @DisplayName("deveCriarServicoComSucesso")
    void deveCriarServicoComSucesso() throws Exception {
        var servico = Instancio.of(ServicoResponse.class)
                .set(field(ServicoResponse.class, "ativo"), true)
                .create();

        when(service.criar(any())).thenReturn(servico);

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Corte Simples",
                                    "descricao": "Corte básico",
                                    "preco": 30.00,
                                    "duracaoMinutos": 30
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(servico.id().toString()))
                .andExpect(jsonPath("$.nome").value(servico.nome()))
                .andExpect(jsonPath("$.ativo").value(servico.ativo()));

        verify(service, times(1)).criar(any());
    }

    @Test
    @DisplayName("deveListarServicosComSucesso")
    void deveListarServicosComSucesso() throws Exception {
        var servicos = Instancio.ofList(ServicoResponse.class).size(2).create();

        when(service.listar()).thenReturn(servicos);

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("deveBuscarServicoPorIdComSucesso")
    void deveBuscarServicoPorIdComSucesso() throws Exception {
        var servico = Instancio.create(ServicoResponse.class);

        when(service.buscar(servico.id())).thenReturn(servico);

        mockMvc.perform(get("/servicos/{id}", servico.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(servico.id().toString()))
                .andExpect(jsonPath("$.nome").value(servico.nome()));

        verify(service, times(1)).buscar(servico.id());
    }

    @Test
    @DisplayName("deveRetornar404QuandoServicoNaoEncontrado")
    void deveRetornar404QuandoServicoNaoEncontrado() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Serviço não encontrado"));

        mockMvc.perform(get("/servicos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Serviço não encontrado"));
    }

    @Test
    @DisplayName("deveDeletarServicoComSucesso")
    void deveDeletarServicoComSucesso() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/servicos/{id}", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(id);
    }
}
