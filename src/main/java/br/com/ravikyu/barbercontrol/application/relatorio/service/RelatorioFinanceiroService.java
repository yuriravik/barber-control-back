package br.com.ravikyu.barbercontrol.application.relatorio.service;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;
import br.com.ravikyu.barbercontrol.application.pagamento.mapper.PagamentoMapper;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioFinanceiroResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.ResumoBarbeiroFinanceiro;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioFinanceiroService {

    private final PagamentoRepository pagamentoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public RelatorioFinanceiroResponse gerar(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException(
                    "dataInicio não pode ser posterior a dataFim");
        }
        var usuario = usuarioProvider.getUsuarioAutenticado();
        List<Barbeiro> barbeiros = resolverBarbeiros(usuario.getRole(),
                usuario.getId(), usuario.getAdminId(), usuario.getBarbeiroId());

        if (barbeiros.isEmpty()) {
            return new RelatorioFinanceiroResponse(
                    BigDecimal.ZERO, 0L, Map.of(), List.of(), List.of());
        }

        List<UUID> barbeiroIds = barbeiros.stream().map(Barbeiro::getId).toList();
        List<Agendamento> agendamentos = agendamentoRepository.listarPorBarbeiroIds(barbeiroIds);

        List<UUID> agendamentoIds = agendamentos.stream().map(Agendamento::getId).toList();

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        List<Pagamento> pagamentos = pagamentoRepository
                .listarComFiltros(agendamentoIds, inicio, fim);

        List<Pagamento> pagos = pagamentos.stream()
                .filter(p -> p.getStatus() == StatusPagamento.PAGO)
                .toList();

        BigDecimal totalRecebido = pagos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> totalPorFormaPagamento = pagos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getFormaPagamento().name(),
                        Collectors.reducing(BigDecimal.ZERO, Pagamento::getValor, BigDecimal::add)));

        Map<UUID, BigDecimal> totalPagosPorAgendamento = pagos.stream()
                .collect(Collectors.toMap(
                        Pagamento::getAgendamentoId,
                        Pagamento::getValor,
                        BigDecimal::add));

        Map<UUID, BigDecimal> totalPorBarbeiro = agendamentos.stream()
                .collect(Collectors.groupingBy(
                        Agendamento::getBarbeiroId,
                        Collectors.reducing(BigDecimal.ZERO,
                                a -> totalPagosPorAgendamento.getOrDefault(a.getId(), BigDecimal.ZERO),
                                BigDecimal::add)));

        List<ResumoBarbeiroFinanceiro> resumoPorBarbeiro = barbeiros.stream()
                .map(b -> {
                    BigDecimal total = totalPorBarbeiro.getOrDefault(b.getId(), BigDecimal.ZERO);
                    BigDecimal comissao = b.calcularComissao(total);
                    return new ResumoBarbeiroFinanceiro(b.getNome(), total, comissao);
                })
                .toList();

        List<PagamentoResponse> pagamentoResponses = pagamentos.stream()
                .map(PagamentoMapper::toResponse)
                .toList();

        return new RelatorioFinanceiroResponse(
                totalRecebido,
                pagamentos.size(),
                totalPorFormaPagamento,
                resumoPorBarbeiro,
                pagamentoResponses);
    }

    private List<Barbeiro> resolverBarbeiros(Role role, UUID usuarioId, UUID adminId, UUID barbeiroIdVinculado) {
        if (role == Role.BARBEIRO) {
            if (barbeiroIdVinculado == null) {
                return List.of();
            }
            return barbeiroRepository.buscarPorId(barbeiroIdVinculado)
                    .map(List::of)
                    .orElse(List.of());
        } else if (role == Role.SECRETARIA) {
            return barbeiroRepository.listarPorUsuario(adminId);
        } else {
            return barbeiroRepository.listarPorUsuario(usuarioId);
        }
    }
}
