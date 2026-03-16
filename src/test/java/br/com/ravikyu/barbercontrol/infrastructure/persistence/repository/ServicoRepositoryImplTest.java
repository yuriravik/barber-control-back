package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ServicoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoRepositoryImplTest {

    @Mock
    private ServicoJpaRepository jpaRepository;

    @InjectMocks
    private ServicoRepositoryImpl repository;

    @Test
    void deveSalvarServicoComSucesso() {
        var id = UUID.randomUUID();
        var servico = new Servico(null, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);
        var entity = new ServicoEntity(id, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(servico);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Corte Simples", result.getNome());
        assertEquals("Corte básico", result.getDescricao());
        assertEquals(new BigDecimal("30.00"), result.getPreco());
        assertEquals(30, result.getDuracaoMinutos());
        assertTrue(result.isAtivo());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    void deveBuscarServicoPorIdComSucesso() {
        var id = UUID.randomUUID();
        var entity = new ServicoEntity(id, "Barba", "Aparar barba", new BigDecimal("20.00"), 20, false);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Barba", result.get().getNome());
        assertFalse(result.get().isAtivo());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoServicoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveListarServicosComSucesso() {
        var entities = List.of(
                new ServicoEntity(UUID.randomUUID(), "Corte", "Desc1", new BigDecimal("30.00"), 30, true),
                new ServicoEntity(UUID.randomUUID(), "Barba", "Desc2", new BigDecimal("20.00"), 20, true)
        );

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    void deveDeletarServicoComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
