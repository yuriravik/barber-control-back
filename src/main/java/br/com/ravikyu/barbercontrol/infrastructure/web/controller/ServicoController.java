package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.ServicoDto;
import br.com.ravikyu.barbercontrol.application.dto.mapper.ServicoMapper;
import br.com.ravikyu.barbercontrol.application.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @PostMapping
    public ServicoDto criar(@RequestBody ServicoDto dto) {
        var servico = service.criar(ServicoMapper.toDomain(dto));
        return ServicoMapper.toDto(servico);
    }

    @GetMapping
    public List<ServicoDto> listar() {
        return service.listar()
                .stream()
                .map(ServicoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ServicoDto buscar(@PathVariable UUID id) {
        return ServicoMapper.toDto(service.buscar(id));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}