package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {

    List<ServicoEntity> findByUsuarioId(UUID usuarioId);

    Optional<ServicoEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
