package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Usuario {

    private UUID id;
    private String email;
    private String senha;
    private Role role;
    private UUID adminId;

    public Usuario(String email, String senha, Role role) {
        validarEmail(email);
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    private void validarEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    // getters e setId
}
