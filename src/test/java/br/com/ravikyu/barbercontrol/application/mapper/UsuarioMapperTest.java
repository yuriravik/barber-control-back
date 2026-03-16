package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.usuario.mapper.UsuarioMapper;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    @Test
    void deveMapearParaDomainComRoleAdmin() {
        var usuario = UsuarioMapper.toDomain("admin@email.com", "senha123", "ADMIN");

        assertEquals("admin@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(Role.ADMIN, usuario.getRole());
    }

    @Test
    void deveMapearParaDomainComRoleBarbeiro() {
        var usuario = UsuarioMapper.toDomain("barbeiro@email.com", "senha456", "BARBEIRO");

        assertEquals(Role.BARBEIRO, usuario.getRole());
    }

    @Test
    void deveMapearParaDomainComRoleEmMinusculas() {
        var usuario = UsuarioMapper.toDomain("user@email.com", "senha", "admin");

        assertEquals(Role.ADMIN, usuario.getRole());
    }

    @Test
    void deveLancarExcecaoQuandoRoleInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> UsuarioMapper.toDomain("user@email.com", "senha", "GERENTE"));
    }

    @Test
    void deveMapearParaLoginResponse() {
        var response = UsuarioMapper.toResponse("meu-token-jwt");

        assertEquals("meu-token-jwt", response.token());
        assertEquals("Bearer", response.tipo());
    }

    @Test
    void deveMapearParaLoginResponseComTokenVazio() {
        var response = UsuarioMapper.toResponse("");

        assertEquals("", response.token());
        assertEquals("Bearer", response.tipo());
    }
}
