package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Servico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoRepository {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(UUID id);

    List<Servico> listar();

    void deletar(UUID id);
}