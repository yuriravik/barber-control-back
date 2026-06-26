package br.com.ravikyu.barbercontrol.application.agendamento.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.AtualizarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.mapper.AgendamentoMapper;
import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import br.com.ravikyu.barbercontrol.application.common.util.PaginationUtils;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.AgendamentoException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final BarbeiroRepository barbeiroRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public AgendamentoResponse criar(CriarAgendamentoRequest request) {
        var agenda = prepararAgendamentoParaPersistencia(AgendamentoMapper.toDomain(request));

        if (repository.existeConflitoHorario(agenda.getBarbeiroId(), agenda.getDataHoraInicio(), agenda.getDataHoraFim())) {
            throw new AgendamentoException("Já existe um agendamento neste horário para o barbeiro informado");
        }

        var id = repository.salvar(agenda).getId();
        return buscar(id);
    }

    public List<AgendamentoResponse> listar() {
        return resolverAgendamentosPermitidos().stream()
                .map(agendamento -> buscar(agendamento.getId()))
                .toList();
    }

    public PageResponse<AgendamentoResponse> buscarPaginado(String status, UUID barbeiroId, UUID servicoId,
                                                            LocalDateTime dataInicio, LocalDateTime dataFim,
                                                            int page, int size) {
        var barbeiroIdsPermitidos = resolverBarbeiroIdsPermitidos();
        var responses = repository.listarComFiltros(barbeiroIdsPermitidos, barbeiroId, servicoId, dataInicio, dataFim)
                .stream()
                .filter(agendamento -> status == null || agendamento.getStatus().name().equalsIgnoreCase(status))
                .map(agendamento -> buscar(agendamento.getId()))
                .toList();
        return PaginationUtils.paginate(responses, page, size);
    }

    public AgendamentoResponse buscar(UUID id) {
        var agendamento = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
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

    public AgendamentoResponse atualizar(UUID id, AtualizarAgendamentoRequest request) {
        var existente = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (existente.getStatus() != StatusAgendamento.AGENDADO) {
            throw new BusinessException("Apenas agendamentos com status AGENDADO podem ser remarcados");
        }

        existente.setClienteId(request.clienteId());
        existente.setBarbeiroId(request.barbeiroId());
        existente.setServicoId(request.servicoId());
        existente.setDataHoraInicio(request.dataHora());
        prepararAgendamentoParaPersistencia(existente);

        if (repository.existeConflitoHorarioExceto(id, existente.getBarbeiroId(), existente.getDataHoraInicio(), existente.getDataHoraFim())) {
            throw new AgendamentoException("Já existe um agendamento neste horário para o barbeiro informado");
        }

        repository.salvar(existente);
        return buscar(id);
    }

    public void concluir(UUID id) {
        var agendamento = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new BusinessException("Apenas agendamentos com status AGENDADO podem ser concluídos");
        }

        var usuario = usuarioProvider.getUsuarioAutenticado();
        if (usuario.getRole() == Role.BARBEIRO) {
            boolean semVinculo = usuario.getBarbeiroId() == null;
            boolean agendamentoDeOutro = !semVinculo && !usuario.getBarbeiroId().equals(agendamento.getBarbeiroId());
            if (semVinculo || agendamentoDeOutro) {
                throw new BusinessException("Barbeiro só pode concluir seus próprios agendamentos");
            }
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        repository.salvar(agendamento);
    }

    public void cancelar(UUID id) {
        var agendamento = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new BusinessException("Apenas agendamentos com status AGENDADO podem ser cancelados");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        repository.salvar(agendamento);
    }

    public void deletar(UUID id) {
        repository.deletar(id);
    }

    private Agendamento prepararAgendamentoParaPersistencia(Agendamento agendamento) {
        validarEntidadesRelacionadas(agendamento.getClienteId(), agendamento.getBarbeiroId(), agendamento.getServicoId());
        var servico = servicoRepository.buscarPorId(agendamento.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        agendamento.setDataHoraFim(agendamento.getDataHoraInicio().plusMinutes(servico.getDuracaoMinutos()));
        return agendamento;
    }

    private void validarEntidadesRelacionadas(UUID clienteId, UUID barbeiroId, UUID servicoId) {
        clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        barbeiroRepository.buscarPorId(barbeiroId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));
        servicoRepository.buscarPorId(servicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
    }

    private List<UUID> resolverBarbeiroIdsPermitidos() {
        var usuario = usuarioProvider.getUsuarioAutenticado();
        if (usuario.getRole() == Role.BARBEIRO) {
            return usuario.getBarbeiroId() == null ? List.of() : List.of(usuario.getBarbeiroId());
        }
        if (usuario.getRole() == Role.SECRETARIA) {
            return barbeiroRepository.listarPorUsuario(usuario.getAdminId())
                    .stream()
                    .map(Barbeiro::getId)
                    .toList();
        }
        return barbeiroRepository.listarPorUsuario(usuario.getId())
                .stream()
                .map(Barbeiro::getId)
                .toList();
    }

    private List<Agendamento> resolverAgendamentosPermitidos() {
        var usuario = usuarioProvider.getUsuarioAutenticado();
        if (usuario.getRole() == Role.BARBEIRO) {
            if (usuario.getBarbeiroId() == null) {
                return List.of();
            }
            return repository.listarPorBarbeiroId(usuario.getBarbeiroId());
        }

        return repository.listarPorBarbeiroIds(resolverBarbeiroIdsPermitidos());
    }
}
