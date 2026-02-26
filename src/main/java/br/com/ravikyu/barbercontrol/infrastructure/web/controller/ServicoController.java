package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.mapper.ServicoMapper;
import br.com.ravikyu.barbercontrol.application.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.UUID;

import static br.com.ravikyu.barbercontrol.application.mapper.ServicoMapper.*;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @PostMapping
    public ServicoResponse criar(@RequestBody CriarServicoRequest dto) {
        var servico = service.criar(toRequest(dto));
        return toDomain(servico);
    }

    @GetMapping
    public List<ServicoResponse> listar() {
        return service.listar()
                .stream()
                .map(ServicoMapper::toDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ServicoResponse buscar(@PathVariable UUID id) {
        return toDomain(service.buscar(id));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}