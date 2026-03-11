package br.com.ravikyu.barbercontrol.application.cliente.service;

import br.com.ravikyu.barbercontrol.application.cliente.dto.*;
import br.com.ravikyu.barbercontrol.application.cliente.mapper.ClienteMapper;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteResponse criar(CriarClienteRequest dto) {

        var cliente = ClienteMapper.toDomain(dto);
        var salvo = repository.salvar(cliente);

        return ClienteMapper.toResponse(salvo);
    }

    public List<ClienteResponse> listar() {
        return repository.listar()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public ClienteResponse buscar(UUID id) {
        var cliente = repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return ClienteMapper.toResponse(cliente);
    }

    public void deletar(UUID id) {
        repository.deletar(id);
    }
}
