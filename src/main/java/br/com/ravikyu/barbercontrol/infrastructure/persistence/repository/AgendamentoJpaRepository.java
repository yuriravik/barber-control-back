package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoEntity, UUID> {

    List<AgendamentoEntity> findByBarbeiroId(UUID barbeiroId);

    List<AgendamentoEntity> findByBarbeiroIdIn(List<UUID> barbeiroIds);
}
