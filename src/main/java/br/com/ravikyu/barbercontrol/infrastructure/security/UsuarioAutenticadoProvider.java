package br.com.ravikyu.barbercontrol.infrastructure.security;

import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioAutenticadoProvider {

    private final UsuarioRepository usuarioRepository;

    public UUID getUsuarioIdAutenticado() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.buscarPorEmail(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
    }
}
