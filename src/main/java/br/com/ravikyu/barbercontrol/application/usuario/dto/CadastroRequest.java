package br.com.ravikyu.barbercontrol.application.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastroRequest(

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotBlank(message = "Role é obrigatória")
        @Pattern(regexp = "ADMIN|BARBEIRO", message = "Role inválida. Valores aceitos: ADMIN, BARBEIRO")
        String role
) {}
