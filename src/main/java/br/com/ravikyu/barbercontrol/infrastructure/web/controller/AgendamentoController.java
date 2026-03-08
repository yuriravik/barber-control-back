package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    public AgendamentoResponse criar(@RequestBody CriarAgendamentoRequest agendamento) {
        return service.criar(agendamento);
    }

    @GetMapping
    public List<AgendamentoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}