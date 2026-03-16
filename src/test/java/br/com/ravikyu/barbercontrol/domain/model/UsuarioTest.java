package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComSucesso() {
        var usuario = new Usuario("usuario@email.com", "senha123", Role.ADMIN);

        assertEquals("usuario@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(Role.ADMIN, usuario.getRole());
    }

    @Test
    void deveCriarUsuarioComRoleBarbeiro() {
        var usuario = new Usuario("barbeiro@email.com", "senha456", Role.BARBEIRO);

        assertEquals(Role.BARBEIRO, usuario.getRole());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNulo() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario(null, "senha123", Role.ADMIN));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEmailSemArroba() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario("emailinvalido", "senha123", Role.ADMIN));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    void devePermitirAlterarEmail() {
        var usuario = new Usuario("original@email.com", "senha", Role.ADMIN);
        usuario.setEmail("novo@email.com");

        assertEquals("novo@email.com", usuario.getEmail());
    }

    @Test
    void devePermitirAlterarSenha() {
        var usuario = new Usuario("usuario@email.com", "senhaAntiga", Role.ADMIN);
        usuario.setSenha("senhaNova");

        assertEquals("senhaNova", usuario.getSenha());
    }
}
