package br.com.ravikyu.barbercontrol.application.cliente.mapper;

import br.com.ravikyu.barbercontrol.application.cliente.dto.*;
import br.com.ravikyu.barbercontrol.domain.cliente.model.Cliente;

public class ClienteMapper {

    private ClienteMapper() {}

    public static Cliente toDomain(CriarClienteRequest request) {
        return new Cliente(
                request.nome(),
                request.email(),
                request.telefone()
        );
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }
}