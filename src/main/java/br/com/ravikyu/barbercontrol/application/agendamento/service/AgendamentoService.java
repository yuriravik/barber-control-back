package br.com.ravikyu.barbercontrol.application.agendamento.service;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.mapper.AgendamentoMapper;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
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

    public AgendamentoResponse criar(CriarAgendamentoRequest request) {
        var agenda = AgendamentoMapper.toDomain(request);
        var servico = servicoRepository.buscarPorId(agenda.getServicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        agenda.setDataHoraFim(
                agenda.getDataHoraInicio().plusMinutes(servico.getDuracaoMinutos())
        );

        var id = repository.salvar(agenda).getId();
        return buscar(id);
    }

    public List<AgendamentoResponse> listar() {
        return repository.listar()
                .stream()
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
