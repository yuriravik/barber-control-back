package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService service;

    @Test
    void deveCriarClienteComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var response = new ClienteResponse(id, "João", "joao@email.com", "11999999999");

        when(service.criar(any())).thenReturn(response);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João",
                                    "email": "joao@email.com",
                                    "telefone": "11999999999"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.telefone").value("11999999999"));

        verify(service, times(1)).criar(any());
    }

    @Test
    void deveRetornar400QuandoNomeAusente() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "joao@email.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoEmailAusente() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João",
                                    "email": "emailinvalido"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarClientesComSucesso() throws Exception {
        var clientes = List.of(
                new ClienteResponse(UUID.randomUUID(), "Maria", "maria@email.com", "21999999999"),
                new ClienteResponse(UUID.randomUUID(), "Pedro", "pedro@email.com", "31999999999")
        );

        when(service.listar()).thenReturn(clientes);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Maria"))
                .andExpect(jsonPath("$[1].nome").value("Pedro"));

        verify(service, times(1)).listar();
    }

    @Test
    void deveBuscarClientePorIdComSucesso() throws Exception {
        var id = UUID.randomUUID();
        var response = new ClienteResponse(id, "Ana", "ana@email.com", "41999999999");

        when(service.buscar(id)).thenReturn(response);

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Ana"));

        verify(service, times(1)).buscar(id);
    }

    @Test
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cliente não encontrado"));
    }

    @Test
    void deveDeletarClienteComSucesso() throws Exception {
        var id = UUID.randomUUID();

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).deletar(id);
    }
}
