package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.usuario.dto.AlterarSenhaRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastrarFuncionarioRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastroRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody @Valid CadastroRequest dto) {
        return service.cadastrar(dto);
    }

    @PostMapping("/cadastrar-funcionario")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarFuncionario(@RequestBody @Valid CadastrarFuncionarioRequest dto) {
        return service.cadastrarFuncionario(dto);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest dto) {
        return service.login(dto);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh() {
        return service.refreshToken();
    }

    @PatchMapping("/alterar-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@RequestBody @Valid AlterarSenhaRequest dto) {
        service.alterarSenha(dto);
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return service.buscarAutenticado();
    }
}
