package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServicoRepositoryImpl implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;

    @Override
    public Servico salvar(Servico servico) {

        ServicoEntity entity = new ServicoEntity(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                servico.isAtivo()
        );

        ServicoEntity salvo = jpaRepository.save(entity);

        return new Servico(
                salvo.getId(),
                salvo.getNome(),
                salvo.getDescricao(),
                salvo.getPreco(),
                salvo.getDuracaoMinutos(),
                salvo.isAtivo()
        );
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new Servico(
                        e.getId(),
                        e.getNome(),
                        e.getDescricao(),
                        e.getPreco(),
                        e.getDuracaoMinutos(),
                        e.isAtivo()
                ));
    }

    @Override
    public List<Servico> listar() {
        return jpaRepository.findAll()
                .stream()
                .map(e -> new Servico(
                        e.getId(),
                        e.getNome(),
                        e.getDescricao(),
                        e.getPreco(),
                        e.getDuracaoMinutos(),
                        e.isAtivo()
                ))
                .toList();
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
}