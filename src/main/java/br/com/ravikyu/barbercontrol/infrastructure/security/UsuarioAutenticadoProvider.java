package br.com.ravikyu.barbercontrol.infrastructure.security;

import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
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

    /**
     * Returns the admin's user ID for BARBEIRO and SECRETARIA users (resolved from their linked adminId),
     * or the user's own ID for ADMIN users. This is used to scope data access so that
     * a BARBEIRO or SECRETARIA user can access the clientes and serviços registered by their linked admin.
     */
    public UUID getAdminUsuarioIdAutenticado() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));

        if ((usuario.getRole() == Role.BARBEIRO || usuario.getRole() == Role.SECRETARIA) && usuario.getAdminId() != null) {
            return usuario.getAdminId();
        }
        return usuario.getId();
    }
}
