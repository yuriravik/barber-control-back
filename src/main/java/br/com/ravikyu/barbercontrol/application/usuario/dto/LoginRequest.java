package br.com.ravikyu.barbercontrol.application.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        String email,

        @NotBlank
        String senha
) {}
