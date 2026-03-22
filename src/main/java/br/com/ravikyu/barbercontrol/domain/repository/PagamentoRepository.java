package br.com.ravikyu.barbercontrol.domain.repository;

import br.com.ravikyu.barbercontrol.domain.model.Pagamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository {

    Pagamento salvar(Pagamento pagamento);

    Optional<Pagamento> buscarPorId(UUID id);

    Optional<Pagamento> buscarPorAgendamentoId(UUID agendamentoId);

    List<Pagamento> listar();

    List<Pagamento> listarComFiltros(List<UUID> agendamentoIds, LocalDateTime dataInicio,
                                     LocalDateTime dataFim);
}
