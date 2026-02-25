package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.cliente.dto.*;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ClienteResponse criar(@RequestBody @Valid CriarClienteRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}