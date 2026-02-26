package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.mapper.AgendamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final ServicoRepository servicoRepository;

    public Agendamento criar(CriarAgendamentoRequest agendamento) {


        var agenda = AgendamentoMapper.toDomain(agendamento);
        var servico = servicoRepository.buscarPorId(agenda.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        agenda.setDataHoraFim(
                agenda.getDataHoraInicio()
                        .plusMinutes(servico.getDuracaoMinutos())
        );

        agenda.setStatus("AGENDADO");

        return repository.salvar(agendamento);
    }

    public List<Agendamento> listar() {
        return repository.listar();
    }
}