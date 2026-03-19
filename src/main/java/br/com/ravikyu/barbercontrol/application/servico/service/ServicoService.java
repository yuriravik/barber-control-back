package br.com.ravikyu.barbercontrol.application.servico.service;

import br.com.ravikyu.barbercontrol.application.servico.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.mapper.ServicoMapper;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public ServicoResponse criar(CriarServicoRequest dto) {
        var servico = ServicoMapper.toDomain(dto);
        servico.setUsuarioId(usuarioProvider.getUsuarioIdAutenticado());
        var salvo = repository.salvar(servico);
        return ServicoMapper.toResponse(salvo);
    }

    public List<ServicoResponse> listar() {
        return repository.listarPorUsuario(usuarioProvider.getUsuarioIdAutenticado())
                .stream()
                .map(ServicoMapper::toResponse)
                .toList();
    }

    public ServicoResponse buscar(UUID id) {
        var usuarioId = usuarioProvider.getUsuarioIdAutenticado();
        var servico = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        return ServicoMapper.toResponse(servico);
    }

    public void deletar(UUID id) {
        var usuarioId = usuarioProvider.getUsuarioIdAutenticado();
        repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        repository.deletar(id);
    }
}
