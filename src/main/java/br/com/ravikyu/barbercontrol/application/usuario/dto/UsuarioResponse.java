package br.com.ravikyu.barbercontrol.application.usuario.dto;

import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String email,
        Role role
) {}
