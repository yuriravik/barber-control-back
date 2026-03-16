package br.com.ravikyu.barbercontrol.application.usuario.service;

import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastroRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.application.usuario.mapper.UsuarioMapper;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UsuarioResponse cadastrar(CadastroRequest dto) {
        if (repository.existePorEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado");
        }

        var usuario = UsuarioMapper.toDomain(dto.email(), passwordEncoder.encode(dto.senha()), dto.role());
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
}
