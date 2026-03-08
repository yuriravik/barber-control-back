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

        ClienteEntity entity = new ClienteEntity(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail()
        );

        ClienteEntity salvo = jpaRepository.save(entity);

        return new Cliente(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone()
        );
    }

    @Override
    public List<Cliente> listar() {
        return jpaRepository.findAll()
                .stream()
                .map(e -> new Cliente(
                        e.getId(),
                        e.getNome(),
                        e.getTelefone(),
                        e.getEmail()
                ))
                .toList();
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new Cliente(
                        e.getId(),
                        e.getNome(),
                        e.getTelefone(),
                        e.getEmail()
                ));
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
}