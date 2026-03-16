package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    List<ClienteEntity> findByUsuarioId(UUID usuarioId);

    Optional<ClienteEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
