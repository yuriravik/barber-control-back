package br.com.ravikyu.barbercontrol.application.pagamento.service;

import br.com.ravikyu.barbercontrol.application.pagamento.dto.PagamentoResponse;
import br.com.ravikyu.barbercontrol.application.pagamento.dto.RegistrarPagamentoRequest;
import br.com.ravikyu.barbercontrol.application.pagamento.mapper.PagamentoMapper;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository repository;
    private final AgendamentoRepository agendamentoRepository;

    public PagamentoResponse registrar(RegistrarPagamentoRequest request) {
        agendamentoRepository.buscarPorId(request.agendamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        repository.buscarPorAgendamentoId(request.agendamentoId())
                .ifPresent(p -> {
                    throw new BusinessException("Já existe um pagamento para este agendamento");
                });

        var pagamento = PagamentoMapper.toDomain(request);
        pagamento.confirmarPagamento();
        var salvo = repository.salvar(pagamento);
        return PagamentoMapper.toResponse(salvo);
    }

    public List<PagamentoResponse> listar() {
        return repository.listar()
                .stream()
                .map(PagamentoMapper::toResponse)
                .toList();
    }

    public PagamentoResponse buscar(UUID id) {
        var pagamento = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
        return PagamentoMapper.toResponse(pagamento);
    }
}
