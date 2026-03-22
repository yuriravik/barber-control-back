package br.com.ravikyu.barbercontrol.application.relatorio.dto;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record RelatorioFinanceiroResponse(
        BigDecimal totalRecebido,
        long quantidadePagamentos,
        Map<String, BigDecimal> totalPorFormaPagamento,
        List<ResumoBarbeiroFinanceiro> resumoPorBarbeiro,
        List<PagamentoResponse> pagamentos
) {}
