package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoEntity, UUID> {

    List<AgendamentoEntity> findByBarbeiroId(UUID barbeiroId);

    List<AgendamentoEntity> findByBarbeiroIdIn(List<UUID> barbeiroIds);

    @Query("SELECT a FROM AgendamentoEntity a WHERE " +
           "a.barbeiroId IN :barbeiroIds AND " +
           "(:barbeiroId IS NULL OR a.barbeiroId = :barbeiroId) AND " +
           "(:servicoId IS NULL OR a.servicoId = :servicoId) AND " +
           "(:dataInicio IS NULL OR a.dataHoraInicio >= :dataInicio) AND " +
           "(:dataFim IS NULL OR a.dataHoraInicio <= :dataFim)")
    List<AgendamentoEntity> findComFiltros(
            @Param("barbeiroIds") List<UUID> barbeiroIds,
            @Param("barbeiroId") UUID barbeiroId,
            @Param("servicoId") UUID servicoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(a) > 0 FROM AgendamentoEntity a WHERE " +
           "a.barbeiroId = :barbeiroId AND " +
           "a.status = 'AGENDADO' AND " +
           "a.dataHoraInicio < :dataHoraFim AND " +
           "a.dataHoraFim > :dataHoraInicio")
    boolean existsConflitoHorario(
            @Param("barbeiroId") UUID barbeiroId,
            @Param("dataHoraInicio") LocalDateTime dataHoraInicio,
            @Param("dataHoraFim") LocalDateTime dataHoraFim);
}
