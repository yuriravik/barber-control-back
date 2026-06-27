package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(UUID id);

    List<Agendamento> listar();

    List<Agendamento> listarPorBarbeiroId(UUID barbeiroId);

    List<Agendamento> listarPorBarbeiroIds(List<UUID> barbeiroIds);

    List<Agendamento> listarComFiltros(List<UUID> barbeiroIds, UUID barbeiroId, UUID servicoId,
                                       LocalDateTime dataInicio, LocalDateTime dataFim);

    boolean existeConflitoHorario(UUID barbeiroId, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim);

    boolean existeConflitoHorarioExceto(UUID agendamentoId, UUID barbeiroId, LocalDateTime dataHoraInicio,
                                        LocalDateTime dataHoraFim);

    void deletar(UUID id);
}
