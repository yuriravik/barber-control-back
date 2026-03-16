package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
