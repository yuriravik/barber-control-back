package br.com.ravikyu.barbercontrol.infrastructure.persistence.mapper;

import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;

public class ClienteEntityMapper {

    private ClienteEntityMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        return ClienteEntity.builder()
                .id(cliente.getId())
                .usuarioId(cliente.getUsuarioId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .build();
    }

    public static ClienteResponse toDomain(ClienteEntity entity) {
        return new ClienteResponse(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone()
        );
    }
}
