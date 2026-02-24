package br.com.ravikyu.barbercontrol.domain.cliente.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Cliente {

    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime criadoEm;

    public Cliente(String nome, String email, String telefone) {
        validarEmail(email);
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.criadoEm = LocalDateTime.now();
    }

    private void validarEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    public void alterarTelefone(String telefone) {
        this.telefone = telefone;
    }

    // getters
}
