package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
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

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService service;

    private ClienteResponse responseValido() {
        return Instancio.of(ClienteResponse.class)
                .generate(field(ClienteResponse.class, "email"), gen -> gen.net().email())
                .create();
    }

    @Test
    @DisplayName("deveCriarClienteComSucesso")
    void deveCriarClienteComSucesso() throws Exception {
        var response = responseValido();

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
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.nome").value(response.nome()));

        verify(service, times(1)).criar(any());
    }

    @Test
    @DisplayName("deveRetornar400QuandoNomeAusente")
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
    @DisplayName("deveRetornar400QuandoEmailAusente")
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
    @DisplayName("deveRetornar400QuandoEmailInvalido")
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
    @DisplayName("deveListarClientesComSucesso")
    void deveListarClientesComSucesso() throws Exception {
        var clientes = Instancio.ofList(ClienteResponse.class)
                .size(2)
                .generate(field(ClienteResponse.class, "email"), gen -> gen.net().email())
                .create();

        when(service.listar()).thenReturn(clientes);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("deveBuscarClientePorIdComSucesso")
    void deveBuscarClientePorIdComSucesso() throws Exception {
        var response = responseValido();

        when(service.buscar(response.id())).thenReturn(response);

        mockMvc.perform(get("/clientes/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.nome").value(response.nome()));

        verify(service, times(1)).buscar(response.id());
    }

    @Test
    @DisplayName("deveRetornar404QuandoClienteNaoEncontrado")
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cliente não encontrado"));
    }

    @Test
    @DisplayName("deveDeletarClienteComSucesso")
    void deveDeletarClienteComSucesso() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).deletar(id);
    }
}
