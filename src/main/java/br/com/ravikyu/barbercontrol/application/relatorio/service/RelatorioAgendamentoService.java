package br.com.ravikyu.barbercontrol.application.relatorio.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.relatorio.dto.RelatorioAgendamentoResponse;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public RelatorioAgendamentoResponse gerar(LocalDate dataInicio, LocalDate dataFim,
                                              UUID barbeiroId, UUID servicoId) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException(
                    "dataInicio não pode ser posterior a dataFim");
        }
        var usuario = usuarioProvider.getUsuarioAutenticado();
        List<UUID> barbeiroIds = resolverBarbeiroIds(usuario.getRole(),
                usuario.getId(), usuario.getAdminId(), usuario.getBarbeiroId());

        if (barbeiroIds.isEmpty()) {
            return new RelatorioAgendamentoResponse(List.of(), 0, Map.of());
        }

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        List<Agendamento> agendamentos = agendamentoRepository
                .listarComFiltros(barbeiroIds, barbeiroId, servicoId, inicio, fim);

        List<AgendamentoResponse> respostas = agendamentos.stream()
                .map(this::enriquecer)
                .toList();

        Map<String, Long> totalPorStatus = agendamentos.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));

        return new RelatorioAgendamentoResponse(respostas, agendamentos.size(), totalPorStatus);
    }

    private List<UUID> resolverBarbeiroIds(Role role, UUID usuarioId, UUID adminId, UUID barbeiroIdVinculado) {
        if (role == Role.BARBEIRO) {
            if (barbeiroIdVinculado == null) {
                return List.of();
            }
            return List.of(barbeiroIdVinculado);
        } else if (role == Role.SECRETARIA) {
            return barbeiroRepository.listarPorUsuario(adminId)
                    .stream().map(Barbeiro::getId).toList();
        } else {
            return barbeiroRepository.listarPorUsuario(usuarioId)
                    .stream().map(Barbeiro::getId).toList();
        }
    }

    private AgendamentoResponse enriquecer(Agendamento agendamento) {
        var cliente = clienteRepository.buscarPorId(agendamento.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        var barbeiro = barbeiroRepository.buscarPorId(agendamento.getBarbeiroId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));
        var servico = servicoRepository.buscarPorId(agendamento.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        return new AgendamentoResponse(
                agendamento.getId(),
                cliente.getNome(),
                barbeiro.getNome(),
                servico.getNome(),
                agendamento.getDataHoraInicio(),
                agendamento.getDataHoraFim(),
                agendamento.getStatus().name()
        );
    }
}
