package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;
import br.com.ravikyu.barbercontrol.application.pagamento.dto.RegistrarPagamentoRequest;
import br.com.ravikyu.barbercontrol.application.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse registrar(@RequestBody @Valid RegistrarPagamentoRequest request) {
        return service.registrar(request);
    }

    @GetMapping
    public List<PagamentoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }
}
