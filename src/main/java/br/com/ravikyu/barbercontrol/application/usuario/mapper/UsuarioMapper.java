package br.com.ravikyu.barbercontrol.application.usuario.mapper;

import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.application.usuario.dto.*;

public class UsuarioMapper {

    private UsuarioMapper() {}

    public static Usuario toDomain(String email, String senha, String role) {
        return new Usuario(
                email,
                senha,
                Role.valueOf(role.toUpperCase())
        );
    }

    public static LoginResponse toResponse(String token) {
        return new LoginResponse(token, "Bearer");
    }

    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}