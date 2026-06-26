package br.com.ravikyu.barbercontrol.application.barbeiro.service;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.AtualizarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.mapper.BarbeiroMapper;
import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import br.com.ravikyu.barbercontrol.application.common.util.PaginationUtils;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarbeiroService {

    private final BarbeiroRepository repository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public BarbeiroResponse criar(CriarBarbeiroRequest dto) {
        var barbeiro = BarbeiroMapper.toDomain(dto);
        barbeiro.setUsuarioId(usuarioProvider.getUsuarioIdAutenticado());
        var salvo = repository.salvar(barbeiro);
        return BarbeiroMapper.toResponse(salvo);
    }

    public List<BarbeiroResponse> listar() {
        return repository.listarPorUsuario(usuarioProvider.getUsuarioIdAutenticado())
                .stream()
                .map(BarbeiroMapper::toResponse)
                .toList();
    }

    public PageResponse<BarbeiroResponse> buscarPaginado(String nome, Boolean ativo, int page, int size) {
        var filtrados = repository.listarPorUsuario(usuarioProvider.getUsuarioIdAutenticado())
                .stream()
                .filter(barbeiro -> nome == null || barbeiro.getNome().toLowerCase(Locale.ROOT).contains(nome.toLowerCase(Locale.ROOT)))
                .filter(barbeiro -> ativo == null || barbeiro.isAtivo() == ativo)
                .map(BarbeiroMapper::toResponse)
                .toList();
        return PaginationUtils.paginate(filtrados, page, size);
    }

    public BarbeiroResponse buscar(UUID id) {
        return repository.buscarPorIdEUsuario(id, usuarioProvider.getUsuarioIdAutenticado())
                .map(BarbeiroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));
    }

    public BarbeiroResponse atualizar(UUID id, AtualizarBarbeiroRequest dto) {
        var usuarioId = usuarioProvider.getUsuarioIdAutenticado();
        var existente = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));

        var atualizado = new Barbeiro(
                existente.getId(),
                dto.nome(),
                dto.especialidade(),
                dto.percentualComissao(),
                existente.isAtivo()
        );
        atualizado.setUsuarioId(usuarioId);

        return BarbeiroMapper.toResponse(repository.salvar(atualizado));
    }

    public void desativar(UUID id) {
        var usuarioId = usuarioProvider.getUsuarioIdAutenticado();
        var barbeiro = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));

        var atualizado = new Barbeiro(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getPercentualComissao(),
                false
        );
        atualizado.setUsuarioId(usuarioId);

        repository.salvar(atualizado);
    }

    public void deletar(UUID id) {
        desativar(id);
    }
}
