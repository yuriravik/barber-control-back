package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(UUID id);

    boolean existePorEmail(String email);
}
