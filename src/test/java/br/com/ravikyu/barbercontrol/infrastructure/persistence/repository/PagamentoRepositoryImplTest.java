package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.PagamentoEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoRepositoryImplTest {

    @Mock
    private PagamentoJpaRepository jpaRepository;

    @InjectMocks
    private PagamentoRepositoryImpl repository;

    private PagamentoEntity entidadeValida() {
        return Instancio.of(PagamentoEntity.class)
                .set(field(PagamentoEntity.class, "formaPagamento"), "PIX")
                .set(field(PagamentoEntity.class, "status"), "PAGO")
                .set(field(PagamentoEntity.class, "valor"), new BigDecimal("80.00"))
                .create();
    }

    @Test
    @DisplayName("deveSalvarPagamentoComSucesso")
    void deveSalvarPagamentoComSucesso() {
        var entity = entidadeValida();
        var pagamento = new br.com.ravikyu.barbercontrol.domain.model.Pagamento(
                entity.getAgendamentoId(), entity.getValor(), FormaPagamento.PIX);
        pagamento.setId(entity.getId());
        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setPagoEm(entity.getPagoEm());

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(pagamento);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(FormaPagamento.PIX, result.getFormaPagamento());
        assertEquals(StatusPagamento.PAGO, result.getStatus());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveBuscarPagamentoPorIdComSucesso")
    void deveBuscarPagamentoPorIdComSucesso() {
        var entity = entidadeValida();

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(FormaPagamento.PIX, result.get().getFormaPagamento());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoPagamentoNaoEncontrado")
    void deveRetornarVazioQuandoPagamentoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarPagamentoPorAgendamentoIdComSucesso")
    void deveBuscarPagamentoPorAgendamentoIdComSucesso() {
        var entity = entidadeValida();

        when(jpaRepository.findByAgendamentoId(entity.getAgendamentoId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorAgendamentoId(entity.getAgendamentoId());

        assertTrue(result.isPresent());
        assertEquals(entity.getAgendamentoId(), result.get().getAgendamentoId());
        verify(jpaRepository, times(1)).findByAgendamentoId(entity.getAgendamentoId());
    }

    @Test
    @DisplayName("deveListarPagamentosComSucesso")
    void deveListarPagamentosComSucesso() {
        var entities = List.of(entidadeValida(), entidadeValida());

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaPagamentos")
    void deveRetornarListaVaziaQuandoNaoHaPagamentos() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveListarComFiltrosComSucesso")
    void deveListarComFiltrosComSucesso() {
        var agendamentoId = UUID.randomUUID();
        var entity = entidadeValida();

        when(jpaRepository.findComFiltros(List.of(agendamentoId), null, null))
                .thenReturn(List.of(entity));

        var result = repository.listarComFiltros(List.of(agendamentoId), null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findComFiltros(List.of(agendamentoId), null, null);
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoListaDeAgendamentoIdsVazia")
    void deveRetornarVazioQuandoListaDeAgendamentoIdsVazia() {
        var result = repository.listarComFiltros(List.of(), null, null);

        assertTrue(result.isEmpty());
        verify(jpaRepository, never()).findComFiltros(any(), any(), any());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoListaDeAgendamentoIdsNula")
    void deveRetornarVazioQuandoListaDeAgendamentoIdsNula() {
        var result = repository.listarComFiltros(null, null, null);

        assertTrue(result.isEmpty());
        verify(jpaRepository, never()).findComFiltros(any(), any(), any());
    }

    @Test
    @DisplayName("deveListarComFiltrosDeDatasComSucesso")
    void deveListarComFiltrosDeDatasComSucesso() {
        var agendamentoId = UUID.randomUUID();
        var entity = entidadeValida();
        var dataInicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        var dataFim = LocalDateTime.of(2026, 12, 31, 23, 59);

        when(jpaRepository.findComFiltros(List.of(agendamentoId), dataInicio, dataFim))
                .thenReturn(List.of(entity));

        var result = repository.listarComFiltros(List.of(agendamentoId), dataInicio, dataFim);

        assertEquals(1, result.size());
        verify(jpaRepository, times(1)).findComFiltros(List.of(agendamentoId), dataInicio, dataFim);
    }
}
