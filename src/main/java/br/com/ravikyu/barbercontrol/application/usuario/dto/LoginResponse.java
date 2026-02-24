package br.com.ravikyu.barbercontrol.application.usuario.dto;

public record LoginResponse(
        String token,
        String tipo
) {}