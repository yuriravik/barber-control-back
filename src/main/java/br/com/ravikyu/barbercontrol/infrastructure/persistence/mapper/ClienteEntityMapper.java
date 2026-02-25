package br.com.ravikyu.barbercontrol.infrastructure.persistence.mapper;

import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.domain.cliente.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;

import java.util.UUID;

public class ClienteEntityMapper {

    private ClienteEntityMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail());
    }

    public static ClienteResponse toDomain(ClienteEntity cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }
}