package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
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

import java.math.BigDecimal;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BarbeiroController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class BarbeiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BarbeiroService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

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
                .andExpect(status().isCreated())
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
    @DisplayName("deveBuscarBarbeiroPorIdComSucesso")
    void deveBuscarBarbeiroPorIdComSucesso() throws Exception {
        var response = responseValido();

        when(service.buscar(response.id())).thenReturn(response);

        mockMvc.perform(get("/barbeiros/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()));

        verify(service).buscar(response.id());
    }

    @Test
    @DisplayName("deveAtualizarBarbeiroComSucesso")
    void deveAtualizarBarbeiroComSucesso() throws Exception {
        var response = responseValido();
        var id = response.id();

        when(service.atualizar(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/barbeiros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Carlos Atualizado",
                                    "especialidade": "Barba",
                                    "percentualComissao": 30.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(response.nome()));

        verify(service, times(1)).atualizar(eq(id), any());
    }

    @Test
    @DisplayName("deveRetornar404AoAtualizarBarbeiroNaoEncontrado")
    void deveRetornar404AoAtualizarBarbeiroNaoEncontrado() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        when(service.atualizar(eq(id), any())).thenThrow(new ResourceNotFoundException("Barbeiro não encontrado"));

        mockMvc.perform(put("/barbeiros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Carlos Atualizado",
                                    "especialidade": "Barba",
                                    "percentualComissao": 30.00
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Barbeiro não encontrado"));
    }

    @Test
    @DisplayName("deveDesativarBarbeiroComSucesso")
    void deveDesativarBarbeiroComSucesso() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(service).desativar(id);

        mockMvc.perform(patch("/barbeiros/{id}/desativar", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).desativar(id);
    }

    @Test
    @DisplayName("deveDeletarBarbeiroComSucesso")
    void deveDeletarBarbeiroComSucesso() throws Exception {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(service).deletar(id);

        mockMvc.perform(delete("/barbeiros/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).deletar(id);
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
