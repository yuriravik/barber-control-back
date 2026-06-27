package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.AgendamentoResponse;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.AtualizarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.service.AgendamentoService;
import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/busca")
    public PageResponse<AgendamentoResponse> buscarPaginado(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID barbeiroId,
            @RequestParam(required = false) UUID servicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.buscarPaginado(status, barbeiroId, servicoId, dataInicio, dataFim, page, size);
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable UUID id, @RequestBody @Valid AtualizarAgendamentoRequest dto) {
        return service.atualizar(id, dto);
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
