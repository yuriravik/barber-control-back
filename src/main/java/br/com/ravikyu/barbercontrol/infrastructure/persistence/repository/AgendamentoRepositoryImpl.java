package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.AgendamentoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AgendamentoRepositoryImpl implements AgendamentoRepository {

    private final AgendamentoJpaRepository jpaRepository;

    @Override
    public Agendamento salvar(Agendamento agendamento) {

        AgendamentoEntity entity = new AgendamentoEntity();
        entity.setClienteId(agendamento.getClienteId());
        entity.setBarbeiroId(agendamento.getBarbeiroId());
        entity.setServicoId(agendamento.getServicoId());
        entity.setDataHoraInicio(agendamento.getDataHoraInicio());
        entity.setDataHoraFim(agendamento.getDataHoraFim());
        entity.setStatus(agendamento.getStatus().name());

        AgendamentoEntity salvo = jpaRepository.save(entity);

        return new Agendamento(
                salvo.getId(),
                salvo.getClienteId(),
                salvo.getBarbeiroId(),
                salvo.getServicoId(),
                salvo.getDataHoraInicio(),
                salvo.getDataHoraFim(),
                StatusAgendamento.valueOf(salvo.getStatus())
        );
    }

    @Override
    public Optional<Agendamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new Agendamento(
                        e.getId(),
                        e.getClienteId(),
                        e.getBarbeiroId(),
                        e.getServicoId(),
                        e.getDataHoraInicio(),
                        e.getDataHoraFim(),
                        StatusAgendamento.valueOf(e.getStatus())
                ));
    }

    @Override
    public List<Agendamento> listar() {
        return jpaRepository.findAll()
                .stream()
                .map(e -> new Agendamento(
                        e.getId(),
                        e.getClienteId(),
                        e.getBarbeiroId(),
                        e.getServicoId(),
                        e.getDataHoraInicio(),
                        e.getDataHoraFim(),
                        StatusAgendamento.valueOf(e.getStatus())
                ))
                .toList();
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
}