package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.service.BarbeiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/barbeiros")
@RequiredArgsConstructor
public class BarbeiroController {

    private final BarbeiroService service;

    @PostMapping
    public BarbeiroResponse criar(@RequestBody CriarBarbeiroRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<BarbeiroResponse> listar() {
        return service.listar();
    }

    @PatchMapping("/{id}/desativar")
    public void desativar(@PathVariable UUID id) {
        service.desativar(id);
    }
}