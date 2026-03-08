package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(UUID id);

    List<Agendamento> listar();

    void deletar(UUID id);
}