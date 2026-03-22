package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioAgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioFinanceiroResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioAgendamentoService;
import br.com.ravikyu.barbercontrol.application.relatorio.service.RelatorioFinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioAgendamentoService relatorioAgendamentoService;
    private final RelatorioFinanceiroService relatorioFinanceiroService;

    @GetMapping("/agendamentos")
    public RelatorioAgendamentoResponse relatorioAgendamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) UUID barbeiroId,
            @RequestParam(required = false) UUID servicoId) {
        return relatorioAgendamentoService.gerar(dataInicio, dataFim, barbeiroId, servicoId);
    }

    @GetMapping("/financeiro")
    public RelatorioFinanceiroResponse relatorioFinanceiro(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return relatorioFinanceiroService.gerar(dataInicio, dataFim);
    }
}
