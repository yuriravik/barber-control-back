package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;
import br.com.ravikyu.barbercontrol.application.pagamento.service.PagamentoService;
import br.com.ravikyu.barbercontrol.infrastructure.security.CustomUserDetailsService;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtAuthenticationFilter;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.SecurityConfig;
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
import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private PagamentoResponse responseValido() {
        return Instancio.of(PagamentoResponse.class)
                .set(field(PagamentoResponse.class, "formaPagamento"), "PIX")
                .set(field(PagamentoResponse.class, "status"), "PAGO")
                .set(field(PagamentoResponse.class, "valor"), new BigDecimal("80.00"))
                .create();
    }

    @Test
    @DisplayName("deveRegistrarPagamentoComSucesso")
    void deveRegistrarPagamentoComSucesso() throws Exception {
        var response = responseValido();
        var agendamentoId = UUID.randomUUID();

        when(service.registrar(any())).thenReturn(response);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "agendamentoId": "%s",
                                    "valor": 80.00,
                                    "formaPagamento": "PIX"
                                }
                                """, agendamentoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"))
                .andExpect(jsonPath("$.status").value("PAGO"));

        verify(service, times(1)).registrar(any());
    }

    @Test
    @DisplayName("deveListarPagamentosComSucesso")
    void deveListarPagamentosComSucesso() throws Exception {
        var pagamentos = List.of(responseValido(), responseValido());

        when(service.listar()).thenReturn(pagamentos);

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("deveListarPagamentosVaziosComSucesso")
    void deveListarPagamentosVaziosComSucesso() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("deveBuscarPagamentoPorIdComSucesso")
    void deveBuscarPagamentoPorIdComSucesso() throws Exception {
        var response = responseValido();

        when(service.buscar(response.id())).thenReturn(response);

        mockMvc.perform(get("/pagamentos/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"))
                .andExpect(jsonPath("$.status").value("PAGO"));

        verify(service, times(1)).buscar(response.id());
    }

    @Test
    @DisplayName("deveRetornar404QuandoPagamentoNaoEncontrado")
    void deveRetornar404QuandoPagamentoNaoEncontrado() throws Exception {
        var id = UUID.randomUUID();

        when(service.buscar(id)).thenThrow(new ResourceNotFoundException("Pagamento não encontrado"));

        mockMvc.perform(get("/pagamentos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pagamento não encontrado"));
    }

    @Test
    @DisplayName("deveRetornar403QuandoNaoAutenticado")
    @org.springframework.security.test.context.support.WithAnonymousUser
    void deveRetornar403QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isForbidden());
    }
}
