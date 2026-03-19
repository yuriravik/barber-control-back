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
        var entity = ServicoEntity.builder()
                .id(servico.getId())
                .usuarioId(servico.getUsuarioId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .preco(servico.getPreco())
                .duracaoMinutos(servico.getDuracaoMinutos())
                .ativo(servico.isAtivo())
                .build();

        var salvo = jpaRepository.save(entity);
        return toDomain(salvo);
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Servico> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Servico> listarPorUsuario(UUID usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Servico> buscarPorIdEUsuario(UUID id, UUID usuarioId) {
        return jpaRepository.findByIdAndUsuarioId(id, usuarioId).map(this::toDomain);
    }

    private Servico toDomain(ServicoEntity e) {
        var servico = new Servico(e.getId(), e.getNome(), e.getDescricao(), e.getPreco(), e.getDuracaoMinutos(), e.isAtivo());
        servico.setUsuarioId(e.getUsuarioId());
        return servico;
    }
}
