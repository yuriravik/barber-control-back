package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.service.BarbeiroService;
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

import java.math.BigDecimal;

import static org.instancio.Select.field;
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

    private BarbeiroResponse responseValido() {
        return Instancio.of(BarbeiroResponse.class)
                .generate(field(BarbeiroResponse.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    @Test
    @DisplayName("deveCriarBarbeiroComSucesso")
    void deveCriarBarbeiroComSucesso() throws Exception {
        var response = responseValido();

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
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.nome").value(response.nome()));

        verify(service, times(1)).criar(any());
    }

    @Test
    @DisplayName("deveListarBarbeirosComSucesso")
    void deveListarBarbeirosComSucesso() throws Exception {
        var barbeiros = Instancio.ofList(BarbeiroResponse.class)
                .size(2)
                .generate(field(BarbeiroResponse.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();

        when(service.listar()).thenReturn(barbeiros);

        mockMvc.perform(get("/barbeiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("deveDesativarBarbeiroComSucesso")
    void deveDesativarBarbeiroComSucesso() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(service).desativar(id);

        mockMvc.perform(patch("/barbeiros/{id}/desativar", id))
                .andExpect(status().isOk());

        verify(service, times(1)).desativar(id);
    }

    @Test
    @DisplayName("deveRetornar404AoDesativarBarbeiroNaoEncontrado")
    void deveRetornar404AoDesativarBarbeiroNaoEncontrado() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doThrow(new ResourceNotFoundException("Barbeiro não encontrado")).when(service).desativar(id);

        mockMvc.perform(patch("/barbeiros/{id}/desativar", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Barbeiro não encontrado"));
    }
}
