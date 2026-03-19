package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario salvar(Usuario usuario) {
        var entity = UsuarioEntity.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .role(usuario.getRole().name())
                .build();

        var salvo = jpaRepository.save(entity);

        return toUsuario(salvo);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toUsuario);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private Usuario toUsuario(UsuarioEntity entity) {
        var usuario = new Usuario(entity.getEmail(), entity.getSenha(), Role.valueOf(entity.getRole()));
        usuario.setId(entity.getId());
        return usuario;
    }
}
