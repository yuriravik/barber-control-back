package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> {

    Optional<PagamentoEntity> findByAgendamentoId(UUID agendamentoId);
}
