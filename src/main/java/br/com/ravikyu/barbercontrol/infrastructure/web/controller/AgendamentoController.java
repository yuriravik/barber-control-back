package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    public Agendamento criar(@RequestBody CriarAgendamentoRequest agendamento) {
        return service.criar(agendamento);
    }

    @GetMapping
    public List<Agendamento> listar() {
        return service.listar();
    }
}