package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.AtualizarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
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

    @GetMapping("/busca")
    public PageResponse<BarbeiroResponse> buscarPaginado(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.buscarPaginado(nome, ativo, page, size);
    }

    @GetMapping("/{id}")
    public BarbeiroResponse buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public BarbeiroResponse atualizar(@PathVariable UUID id, @RequestBody @Valid AtualizarBarbeiroRequest dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        service.desativar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}
