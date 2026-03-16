package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/barbeiros")
@RequiredArgsConstructor
public class BarbeiroController {

    private final BarbeiroService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BarbeiroResponse criar(@RequestBody @Valid CriarBarbeiroRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<BarbeiroResponse> listar() {
        return service.listar();
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        service.desativar(id);
    }
}