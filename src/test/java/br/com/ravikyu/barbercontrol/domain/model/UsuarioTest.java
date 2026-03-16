package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("deveCriarUsuarioComSucesso")
    void deveCriarUsuarioComSucesso() {
        var email = Instancio.gen().net().email().get();
        var senha = Instancio.create(String.class);
        var role = Instancio.gen().enumOf(Role.class).get();

        var usuario = new Usuario(email, senha, role);

        assertEquals(email, usuario.getEmail());
        assertEquals(senha, usuario.getSenha());
        assertEquals(role, usuario.getRole());
    }

    @Test
    @DisplayName("deveCriarUsuarioComRoleBarbeiro")
    void deveCriarUsuarioComRoleBarbeiro() {
        var email = Instancio.gen().net().email().get();
        var senha = Instancio.create(String.class);

        var usuario = new Usuario(email, senha, Role.BARBEIRO);

        assertEquals(Role.BARBEIRO, usuario.getRole());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoEmailNulo")
    void deveLancarExcecaoQuandoEmailNulo() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario(null, "senha123", Role.ADMIN));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoEmailSemArroba")
    void deveLancarExcecaoQuandoEmailSemArroba() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario("emailinvalido", "senha123", Role.ADMIN));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    @DisplayName("devePermitirAlterarEmail")
    void devePermitirAlterarEmail() {
        var usuario = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .create();
        var novoEmail = Instancio.gen().net().email().get();

        usuario.setEmail(novoEmail);

        assertEquals(novoEmail, usuario.getEmail());
    }

    @Test
    @DisplayName("devePermitirAlterarSenha")
    void devePermitirAlterarSenha() {
        var usuario = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .create();
        var novaSenha = Instancio.create(String.class);

        usuario.setSenha(novaSenha);

        assertEquals(novaSenha, usuario.getSenha());
    }
}
