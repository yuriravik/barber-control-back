package br.com.ravikyu.barbercontrol.domain.cliente.repository;

import br.com.ravikyu.barbercontrol.domain.cliente.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    List<Cliente> listar();

    Optional<Cliente> buscarPorId(UUID id);

    void deletar(UUID id);
}