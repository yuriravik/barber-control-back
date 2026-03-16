package br.com.ravikyu.barbercontrol.application.servico.service;

import br.com.ravikyu.barbercontrol.application.servico.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.mapper.ServicoMapper;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoResponse criar(CriarServicoRequest dto) {
        var servico = ServicoMapper.toDomain(dto);
        var salvo = repository.salvar(servico);
        return ServicoMapper.toResponse(salvo);
    }

    public List<ServicoResponse> listar() {
        return repository.listar()
                .stream()
                .map(ServicoMapper::toResponse)
                .toList();
    }

    public ServicoResponse buscar(UUID id) {
        var servico = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        return ServicoMapper.toResponse(servico);
    }

    public void deletar(UUID id) {
        repository.deletar(id);
    }
}
