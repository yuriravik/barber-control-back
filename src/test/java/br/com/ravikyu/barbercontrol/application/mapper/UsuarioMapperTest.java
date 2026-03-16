package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.usuario.mapper.UsuarioMapper;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    @Test
    @DisplayName("deveMapearParaDomainComRoleAdmin")
    void deveMapearParaDomainComRoleAdmin() {
        var email = Instancio.gen().net().email().get();
        var senha = Instancio.create(String.class);

        var usuario = UsuarioMapper.toDomain(email, senha, "ADMIN");

        assertEquals(email, usuario.getEmail());
        assertEquals(senha, usuario.getSenha());
        assertEquals(Role.ADMIN, usuario.getRole());
    }

    @Test
    @DisplayName("deveMapearParaDomainComRoleBarbeiro")
    void deveMapearParaDomainComRoleBarbeiro() {
        var email = Instancio.gen().net().email().get();

        var usuario = UsuarioMapper.toDomain(email, Instancio.create(String.class), "BARBEIRO");

        assertEquals(Role.BARBEIRO, usuario.getRole());
    }

    @Test
    @DisplayName("deveMapearParaDomainComRoleEmMinusculas")
    void deveMapearParaDomainComRoleEmMinusculas() {
        var email = Instancio.gen().net().email().get();

        var usuario = UsuarioMapper.toDomain(email, Instancio.create(String.class), "admin");

        assertEquals(Role.ADMIN, usuario.getRole());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoRoleInvalida")
    void deveLancarExcecaoQuandoRoleInvalida() {
        var email = Instancio.gen().net().email().get();

        assertThrows(IllegalArgumentException.class,
                () -> UsuarioMapper.toDomain(email, "senha", "GERENTE"));
    }

    @Test
    @DisplayName("deveMapearParaLoginResponse")
    void deveMapearParaLoginResponse() {
        var token = Instancio.create(String.class);

        var response = UsuarioMapper.toResponse(token);

        assertEquals(token, response.token());
        assertEquals("Bearer", response.tipo());
    }

    @Test
    @DisplayName("deveMapearParaLoginResponseComTokenVazio")
    void deveMapearParaLoginResponseComTokenVazio() {
        var response = UsuarioMapper.toResponse("");

        assertEquals("", response.token());
        assertEquals("Bearer", response.tipo());
    }
}
