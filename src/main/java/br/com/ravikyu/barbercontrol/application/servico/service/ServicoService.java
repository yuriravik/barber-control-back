package br.com.ravikyu.barbercontrol.application.servico.service;

import br.com.ravikyu.barbercontrol.application.servico.dto.AtualizarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.mapper.ServicoMapper;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
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
        servico.setUsuarioId(usuarioProvider.getAdminUsuarioIdAutenticado());
        var salvo = repository.salvar(servico);
        return ServicoMapper.toResponse(salvo);
    }

    public List<ServicoResponse> listar() {
        return repository.listarPorUsuario(usuarioProvider.getAdminUsuarioIdAutenticado())
                .stream()
                .map(ServicoMapper::toResponse)
                .toList();
    }

    public ServicoResponse buscar(UUID id) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        var servico = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        return ServicoMapper.toResponse(servico);
    }

    public ServicoResponse atualizar(UUID id, AtualizarServicoRequest dto) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        var existente = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        var atualizado = new Servico(
                existente.getId(),
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                dto.duracaoMinutos(),
                existente.isAtivo()
        );
        atualizado.setUsuarioId(usuarioId);

        return ServicoMapper.toResponse(repository.salvar(atualizado));
    }

    public void deletar(UUID id) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        repository.deletar(id);
    }
}
