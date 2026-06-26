package br.com.ravikyu.barbercontrol.application.pagamento.service;

import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import br.com.ravikyu.barbercontrol.application.common.util.PaginationUtils;
import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;
import br.com.ravikyu.barbercontrol.application.pagamento.dto.RegistrarPagamentoRequest;
import br.com.ravikyu.barbercontrol.application.pagamento.mapper.PagamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository repository;
    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public PagamentoResponse registrar(RegistrarPagamentoRequest request) {
        agendamentoRepository.buscarPorId(request.agendamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        repository.buscarPorAgendamentoId(request.agendamentoId())
                .ifPresent(p -> {
                    throw new BusinessException("Já existe um pagamento para este agendamento");
                });

        var pagamento = PagamentoMapper.toDomain(request);
        pagamento.confirmarPagamento();
        var salvo = repository.salvar(pagamento);
        return PagamentoMapper.toResponse(salvo);
    }

    public List<PagamentoResponse> listar() {
        var agendamentoIdsPermitidos = resolverAgendamentoIdsPermitidos();
        return repository.listarComFiltros(agendamentoIdsPermitidos, null, null)
                .stream()
                .map(PagamentoMapper::toResponse)
                .toList();
    }

    public PageResponse<PagamentoResponse> buscarPaginado(String status, String formaPagamento,
                                                          LocalDateTime dataInicio, LocalDateTime dataFim,
                                                          int page, int size) {
        var agendamentoIdsPermitidos = resolverAgendamentoIdsPermitidos();
        var pagamentos = repository.listarComFiltros(agendamentoIdsPermitidos, dataInicio, dataFim)
                .stream()
                .filter(pagamento -> status == null || pagamento.getStatus() == StatusPagamento.valueOf(status.toUpperCase()))
                .filter(pagamento -> formaPagamento == null || pagamento.getFormaPagamento() == FormaPagamento.valueOf(formaPagamento.toUpperCase()))
                .map(PagamentoMapper::toResponse)
                .toList();
        return PaginationUtils.paginate(pagamentos, page, size);
    }

    public PagamentoResponse buscar(UUID id) {
        var pagamento = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        if (!usuarioTemAcessoAoPagamento(pagamento.getAgendamentoId())) {
            throw new ResourceNotFoundException("Pagamento não encontrado");
        }

        return PagamentoMapper.toResponse(pagamento);
    }

    private List<UUID> resolverAgendamentoIdsPermitidos() {
        var usuario = usuarioProvider.getUsuarioAutenticado();
        if (usuario.getRole() == Role.BARBEIRO) {
            if (usuario.getBarbeiroId() == null) {
                return List.of();
            }
            return agendamentoRepository.listarPorBarbeiroId(usuario.getBarbeiroId())
                    .stream()
                    .map(Agendamento::getId)
                    .toList();
        }

        var adminId = usuario.getRole() == Role.ADMIN ? usuario.getId() : usuario.getAdminId();
        if (adminId == null) {
            return List.of();
        }

        var barbeiroIds = barbeiroRepository.listarPorUsuario(adminId)
                .stream()
                .map(Barbeiro::getId)
                .toList();
        return agendamentoRepository.listarPorBarbeiroIds(barbeiroIds)
                .stream()
                .map(Agendamento::getId)
                .toList();
    }

    private boolean usuarioTemAcessoAoPagamento(UUID agendamentoId) {
        var usuario = usuarioProvider.getUsuarioAutenticado();
        var agendamento = agendamentoRepository.buscarPorId(agendamentoId).orElse(null);
        if (agendamento == null) {
            return false;
        }

        if (usuario.getRole() == Role.BARBEIRO) {
            return usuario.getBarbeiroId() != null
                    && Objects.equals(agendamento.getBarbeiroId(), usuario.getBarbeiroId());
        }

        var barbeiro = barbeiroRepository.buscarPorId(agendamento.getBarbeiroId()).orElse(null);
        if (barbeiro == null) {
            return false;
        }

        var adminId = usuario.getRole() == Role.ADMIN ? usuario.getId() : usuario.getAdminId();
        return adminId != null && Objects.equals(barbeiro.getUsuarioId(), adminId);
    }
}
