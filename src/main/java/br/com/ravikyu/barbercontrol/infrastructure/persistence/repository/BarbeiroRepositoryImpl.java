package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.BarbeiroEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BarbeiroRepositoryImpl implements BarbeiroRepository {

    private final BarbeiroJpaRepository jpaRepository;

    @Override
    public Barbeiro salvar(Barbeiro barbeiro) {
        var entity = BarbeiroEntity.builder()
                .id(barbeiro.getId())
                .usuarioId(barbeiro.getUsuarioId())
                .nome(barbeiro.getNome())
                .especialidade(barbeiro.getEspecialidade())
                .percentualComissao(barbeiro.getPercentualComissao())
                .ativo(barbeiro.isAtivo())
                .build();

        var salvo = jpaRepository.save(entity);
        return toDomain(salvo);
    }

    @Override
    public Optional<Barbeiro> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Barbeiro> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Barbeiro> listarPorUsuario(UUID usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Barbeiro> buscarPorIdEUsuario(UUID id, UUID usuarioId) {
        return jpaRepository.findByIdAndUsuarioId(id, usuarioId).map(this::toDomain);
    }

    private Barbeiro toDomain(BarbeiroEntity e) {
        var barbeiro = new Barbeiro(
                e.getId(),
                e.getNome(),
                e.getEspecialidade(),
                e.getPercentualComissao(),
                e.isAtivo()
        );
        barbeiro.setUsuarioId(e.getUsuarioId());
        return barbeiro;
    }
}
