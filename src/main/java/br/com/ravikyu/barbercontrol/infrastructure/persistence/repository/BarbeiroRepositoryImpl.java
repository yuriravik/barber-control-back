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

        BarbeiroEntity entity = new BarbeiroEntity(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getPercentualComissao(),
                barbeiro.isAtivo()
        );

        BarbeiroEntity salvo = jpaRepository.save(entity);

        return new Barbeiro(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEspecialidade(),
                salvo.getPercentualComissao(),
                salvo.isAtivo()
        );
    }

    @Override
    public Optional<Barbeiro> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new Barbeiro(
                        e.getId(),
                        e.getNome(),
                        e.getEspecialidade(),
                        e.getPercentualComissao(),
                        e.isAtivo()
                ));
    }

    @Override
    public List<Barbeiro> listar() {
        return jpaRepository.findAll()
                .stream()
                .map(e -> new Barbeiro(
                        e.getId(),
                        e.getNome(),
                        e.getEspecialidade(),
                        e.getPercentualComissao(),
                        e.isAtivo()
                ))
                .toList();
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
}