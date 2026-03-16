package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    @Override
    public Cliente salvar(Cliente cliente) {
        var entity = ClienteEntity.builder()
                .id(cliente.getId())
                .usuarioId(cliente.getUsuarioId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .build();

        var salvo = jpaRepository.save(entity);
        return toDomain(salvo);
    }

    @Override
    public List<Cliente> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Cliente> listarPorUsuario(UUID usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Cliente> buscarPorIdEUsuario(UUID id, UUID usuarioId) {
        return jpaRepository.findByIdAndUsuarioId(id, usuarioId).map(this::toDomain);
    }

    private Cliente toDomain(ClienteEntity e) {
        var cliente = new Cliente(e.getId(), e.getNome(), e.getEmail(), e.getTelefone());
        cliente.setUsuarioId(e.getUsuarioId());
        return cliente;
    }
}
