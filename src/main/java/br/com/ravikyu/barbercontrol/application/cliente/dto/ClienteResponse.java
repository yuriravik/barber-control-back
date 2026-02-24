package br.com.ravikyu.barbercontrol.application.cliente.dto;

import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String email,
        String telefone
) {}
