package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(UUID id);

    List<Agendamento> listar();

    List<Agendamento> listarPorBarbeiroId(UUID barbeiroId);

    List<Agendamento> listarPorBarbeiroIds(List<UUID> barbeiroIds);

    void deletar(UUID id);
}