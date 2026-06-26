package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioAgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioFinanceiroResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.ResumoBarbeiroFinanceiro;
import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioAgendamentoService;
import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioFinanceiroService;
import br.com.ravikyu.barbercontrol.infrastructure.security.CustomUserDetailsService;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtAuthenticationFilter;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.SecurityConfig;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelatorioController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RelatorioAgendamentoService relatorioAgendamentoService;

    @MockitoBean
    private RelatorioFinanceiroService relatorioFinanceiroService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("deveRetornarRelatorioDeAgendamentosComSucesso")
    void deveRetornarRelatorioDeAgendamentosComSucesso() throws Exception {
        var agendamentos = Instancio.ofList(
                br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse.class)
                .size(3)
                .set(org.instancio.Select.field(
                        br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse.class, "status"),
                        "AGENDADO")
                .create();
        var relatorio = new RelatorioAgendamentoResponse(agendamentos, 3, Map.of("AGENDADO", 3L));

        when(relatorioAgendamentoService.gerar(null, null, null, null)).thenReturn(relatorio);

        mockMvc.perform(get("/relatorios/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.agendamentos.length()").value(3))
                .andExpect(jsonPath("$.totalPorStatus.AGENDADO").value(3));

        verify(relatorioAgendamentoService, times(1)).gerar(null, null, null, null);
    }

    @Test
    @DisplayName("deveRetornarRelatorioDeAgendamentosVazioComSucesso")
    void deveRetornarRelatorioDeAgendamentosVazioComSucesso() throws Exception {
        var relatorio = new RelatorioAgendamentoResponse(List.of(), 0, Map.of());

        when(relatorioAgendamentoService.gerar(null, null, null, null)).thenReturn(relatorio);

        mockMvc.perform(get("/relatorios/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.agendamentos.length()").value(0));
    }

    @Test
    @DisplayName("deveRetornar422QuandoDataInicioMaiorQueDataFimNoRelatorioAgendamentos")
    void deveRetornar422QuandoDataInicioMaiorQueDataFimNoRelatorioAgendamentos() throws Exception {
        when(relatorioAgendamentoService.gerar(any(), any(), any(), any()))
                .thenThrow(new BusinessException("dataInicio não pode ser posterior a dataFim"));

        mockMvc.perform(get("/relatorios/agendamentos")
                        .param("dataInicio", "2026-12-31")
                        .param("dataFim", "2026-01-01"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("dataInicio não pode ser posterior a dataFim"));
    }

    @Test
    @DisplayName("deveRetornarRelatorioFinanceiroComSucesso")
    void deveRetornarRelatorioFinanceiroComSucesso() throws Exception {
        var resumo = List.of(new ResumoBarbeiroFinanceiro("Carlos", new BigDecimal("500.00"), new BigDecimal("100.00")));
        var relatorio = new RelatorioFinanceiroResponse(
                new BigDecimal("500.00"), 5L,
                Map.of("PIX", new BigDecimal("300.00"), "DINHEIRO", new BigDecimal("200.00")),
                resumo, List.of());

        when(relatorioFinanceiroService.gerar(null, null)).thenReturn(relatorio);

        mockMvc.perform(get("/relatorios/financeiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecebido").value(500.00))
                .andExpect(jsonPath("$.quantidadePagamentos").value(5))
                .andExpect(jsonPath("$.resumoPorBarbeiro.length()").value(1))
                .andExpect(jsonPath("$.resumoPorBarbeiro[0].nomeBarbeiro").value("Carlos"));

        verify(relatorioFinanceiroService, times(1)).gerar(null, null);
    }

    @Test
    @DisplayName("deveRetornarRelatorioFinanceiroVazioComSucesso")
    void deveRetornarRelatorioFinanceiroVazioComSucesso() throws Exception {
        var relatorio = new RelatorioFinanceiroResponse(
                BigDecimal.ZERO, 0L, Map.of(), List.of(), List.of());

        when(relatorioFinanceiroService.gerar(null, null)).thenReturn(relatorio);

        mockMvc.perform(get("/relatorios/financeiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecebido").value(0))
                .andExpect(jsonPath("$.quantidadePagamentos").value(0))
                .andExpect(jsonPath("$.resumoPorBarbeiro.length()").value(0));
    }

    @Test
    @DisplayName("deveRetornar422QuandoDataInicioMaiorQueDataFimNoRelatorioFinanceiro")
    void deveRetornar422QuandoDataInicioMaiorQueDataFimNoRelatorioFinanceiro() throws Exception {
        when(relatorioFinanceiroService.gerar(any(), any()))
                .thenThrow(new BusinessException("dataInicio não pode ser posterior a dataFim"));

        mockMvc.perform(get("/relatorios/financeiro")
                        .param("dataInicio", "2026-12-31")
                        .param("dataFim", "2026-01-01"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("dataInicio não pode ser posterior a dataFim"));
    }

    @Test
    @DisplayName("deveRetornar403QuandoNaoAutenticado")
    @WithAnonymousUser
    void deveRetornar403QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/relatorios/agendamentos"))
                .andExpect(status().isForbidden());
    }
}
