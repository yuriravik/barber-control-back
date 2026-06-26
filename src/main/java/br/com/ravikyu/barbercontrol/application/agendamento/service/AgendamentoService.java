package br.com.ravikyu.barbercontrol.application.agendamento.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.mapper.AgendamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.AgendamentoException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        var agenda = AgendamentoMapper.toDomain(request);
        var servico = servicoRepository.buscarPorId(agenda.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        agenda.setDataHoraFim(
                agenda.getDataHoraInicio().plusMinutes(servico.getDuracaoMinutos())
        );

        if (repository.existeConflitoHorario(agenda.getBarbeiroId(), agenda.getDataHoraInicio(), agenda.getDataHoraFim())) {
            throw new AgendamentoException("Já existe um agendamento neste horário para o barbeiro informado");
        }

        var id = repository.salvar(agenda).getId();
        return buscar(id);
    }

    public List<AgendamentoResponse> listar() {
        var usuario = usuarioProvider.getUsuarioAutenticado();
        List<Agendamento> agendamentos;

        if (usuario.getRole() == Role.BARBEIRO) {
            if (usuario.getBarbeiroId() == null) {
                return List.of();
            }
            agendamentos = repository.listarPorBarbeiroId(usuario.getBarbeiroId());
        } else if (usuario.getRole() == Role.SECRETARIA) {
            var barbeiroIds = barbeiroRepository.listarPorUsuario(usuario.getAdminId())
                    .stream()
                    .map(Barbeiro::getId)
                    .toList();
            agendamentos = repository.listarPorBarbeiroIds(barbeiroIds);
        } else {
            var barbeiroIds = barbeiroRepository.listarPorUsuario(usuario.getId())
                    .stream()
                    .map(Barbeiro::getId)
                    .toList();
            agendamentos = repository.listarPorBarbeiroIds(barbeiroIds);
        }

        return agendamentos.stream()
                .map(a -> buscar(a.getId()))
                .toList();
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

    public void deletar(UUID id) {
        repository.deletar(id);
    }
}
