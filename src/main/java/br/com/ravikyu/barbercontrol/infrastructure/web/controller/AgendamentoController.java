package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@RequestBody @Valid CriarAgendamentoRequest agendamento) {
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

    @PatchMapping("/{id}/concluir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void concluir(@PathVariable UUID id) {
        service.concluir(id);
    }

    @PatchMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable UUID id) {
        service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}