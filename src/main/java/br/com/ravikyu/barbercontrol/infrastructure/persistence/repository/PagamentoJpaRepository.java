package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> {

    Optional<PagamentoEntity> findByAgendamentoId(UUID agendamentoId);

    @Query("SELECT p FROM PagamentoEntity p WHERE " +
           "p.agendamentoId IN :agendamentoIds AND " +
           "(:dataInicio IS NULL OR p.pagoEm >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.pagoEm <= :dataFim)")
    List<PagamentoEntity> findComFiltros(
            @Param("agendamentoIds") List<UUID> agendamentoIds,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}
