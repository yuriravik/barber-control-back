package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.PagamentoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PagamentoRepositoryImpl implements PagamentoRepository {

    private final PagamentoJpaRepository jpaRepository;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        PagamentoEntity entity = PagamentoEntity.builder()
                .id(pagamento.getId())
                .agendamentoId(pagamento.getAgendamentoId())
                .valor(pagamento.getValor())
                .formaPagamento(pagamento.getFormaPagamento().name())
                .status(pagamento.getStatus().name())
                .pagoEm(pagamento.getPagoEm())
                .build();

        PagamentoEntity salvo = jpaRepository.save(entity);
        return toModel(salvo);
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Pagamento> buscarPorAgendamentoId(UUID agendamentoId) {
        return jpaRepository.findByAgendamentoId(agendamentoId).map(this::toModel);
    }

    @Override
    public List<Pagamento> listar() {
        return jpaRepository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public List<Pagamento> listarComFiltros(List<UUID> agendamentoIds, LocalDateTime dataInicio,
                                            LocalDateTime dataFim) {
        if (agendamentoIds == null || agendamentoIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findComFiltros(agendamentoIds, dataInicio, dataFim)
                .stream()
                .map(this::toModel)
                .toList();
    }

    private Pagamento toModel(PagamentoEntity e) {
        var pagamento = new Pagamento(
                e.getAgendamentoId(),
                e.getValor(),
                FormaPagamento.valueOf(e.getFormaPagamento())
        );
        pagamento.setId(e.getId());
        pagamento.setStatus(StatusPagamento.valueOf(e.getStatus()));
        pagamento.setPagoEm(e.getPagoEm());
        return pagamento;
    }
}
