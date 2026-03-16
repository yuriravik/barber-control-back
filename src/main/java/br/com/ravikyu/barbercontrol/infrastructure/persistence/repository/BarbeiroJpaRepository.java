package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.BarbeiroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BarbeiroJpaRepository extends JpaRepository<BarbeiroEntity, UUID> {

    List<BarbeiroEntity> findByUsuarioId(UUID usuarioId);

    Optional<BarbeiroEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
