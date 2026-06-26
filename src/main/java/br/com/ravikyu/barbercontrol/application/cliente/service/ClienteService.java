package br.com.ravikyu.barbercontrol.application.cliente.service;

import br.com.ravikyu.barbercontrol.application.cliente.dto.AtualizarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.dto.ClienteResponse;
import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.mapper.ClienteMapper;
import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import br.com.ravikyu.barbercontrol.application.common.util.PaginationUtils;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public ClienteResponse criar(CriarClienteRequest dto) {
        var cliente = ClienteMapper.toDomain(dto);
        cliente.setUsuarioId(usuarioProvider.getAdminUsuarioIdAutenticado());
        var salvo = repository.salvar(cliente);
        return ClienteMapper.toResponse(salvo);
    }

    public List<ClienteResponse> listar() {
        return repository.listarPorUsuario(usuarioProvider.getAdminUsuarioIdAutenticado())
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public PageResponse<ClienteResponse> buscarPaginado(String nome, String email, String telefone, int page, int size) {
        var clientes = repository.listarPorUsuario(usuarioProvider.getAdminUsuarioIdAutenticado())
                .stream()
                .filter(cliente -> nome == null || cliente.getNome().toLowerCase(Locale.ROOT).contains(nome.toLowerCase(Locale.ROOT)))
                .filter(cliente -> email == null || cliente.getEmail().toLowerCase(Locale.ROOT).contains(email.toLowerCase(Locale.ROOT)))
                .filter(cliente -> telefone == null || (cliente.getTelefone() != null && cliente.getTelefone().contains(telefone)))
                .map(ClienteMapper::toResponse)
                .toList();
        return PaginationUtils.paginate(clientes, page, size);
    }

    public ClienteResponse buscar(UUID id) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        var cliente = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return ClienteMapper.toResponse(cliente);
    }

    public ClienteResponse atualizar(UUID id, AtualizarClienteRequest dto) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        var existente = repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        var atualizado = new Cliente(existente.getId(), dto.nome(), dto.email(), dto.telefone());
        atualizado.setUsuarioId(usuarioId);

        return ClienteMapper.toResponse(repository.salvar(atualizado));
    }

    public void deletar(UUID id) {
        var usuarioId = usuarioProvider.getAdminUsuarioIdAutenticado();
        repository.buscarPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        repository.deletar(id);
    }
}
