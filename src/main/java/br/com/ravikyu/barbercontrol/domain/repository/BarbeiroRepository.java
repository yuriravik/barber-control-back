package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BarbeiroRepository {

    Barbeiro salvar(Barbeiro barbeiro);

    Optional<Barbeiro> buscarPorId(UUID id);

    List<Barbeiro> listar();

    void deletar(UUID id);

    List<Barbeiro> listarPorUsuario(UUID usuarioId);

    Optional<Barbeiro> buscarPorIdEUsuario(UUID id, UUID usuarioId);
}