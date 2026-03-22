package br.com.ravikyu.barbercontrol.application.usuario.service;

import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastrarFuncionarioRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastroRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.application.usuario.mapper.UsuarioMapper;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public UsuarioResponse cadastrar(CadastroRequest dto) {
        if (repository.existePorEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado");
        }

        if (Role.BARBEIRO.name().equalsIgnoreCase(dto.role()) || Role.SECRETARIA.name().equalsIgnoreCase(dto.role())) {
            throw new BusinessException(
                    "Use o endpoint /usuarios/cadastrar-funcionario para criar usuários com role " + dto.role().toUpperCase());
        }

        if (Role.ADMIN.name().equalsIgnoreCase(dto.role()) && dto.adminId() != null) {
            throw new BusinessException("Usuários com role ADMIN não podem ser vinculados a outro administrador");
        }

        var usuario = UsuarioMapper.toDomain(dto.email(), passwordEncoder.encode(dto.senha()), dto.role());
        var salvo = repository.salvar(usuario);
        return UsuarioMapper.toUsuarioResponse(salvo);
    }

    public UsuarioResponse cadastrarFuncionario(CadastrarFuncionarioRequest dto) {
        if (repository.existePorEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado");
        }

        var adminId = usuarioAutenticadoProvider.getUsuarioIdAutenticado();

        if (Role.BARBEIRO.name().equalsIgnoreCase(dto.role()) && dto.barbeiroId() != null) {
            if (repository.buscarPorId(adminId).isEmpty()) {
                throw new ResourceNotFoundException("Administrador não encontrado");
            }
        }

        var usuario = UsuarioMapper.toDomain(dto.email(), passwordEncoder.encode(dto.senha()), dto.role());
        usuario.setAdminId(adminId);
        usuario.setBarbeiroId(dto.barbeiroId());
        var salvo = repository.salvar(usuario);
        return UsuarioMapper.toUsuarioResponse(salvo);
    }

    public LoginResponse login(LoginRequest dto) {
        var usuario = repository.buscarPorEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas");
        }

        var token = tokenProvider.gerarToken(usuario.getEmail());
        return UsuarioMapper.toResponse(token);
    }

    public UsuarioResponse buscarAutenticado() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var usuario = repository.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return UsuarioMapper.toUsuarioResponse(usuario);
    }
}
