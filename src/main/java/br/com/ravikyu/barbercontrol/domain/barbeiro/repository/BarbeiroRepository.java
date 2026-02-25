package br.com.ravikyu.barbercontrol.domain.barbeiro.repository;

import br.com.ravikyu.barbercontrol.domain.barbeiro.model.Barbeiro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BarbeiroRepository {

    Barbeiro salvar(Barbeiro barbeiro);

    Optional<Barbeiro> buscarPorId(UUID id);

    List<Barbeiro> listar();

    void deletar(UUID id);
}