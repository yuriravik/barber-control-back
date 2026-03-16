package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.BarbeiroEntity;
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
class BarbeiroRepositoryImplTest {

    @Mock
    private BarbeiroJpaRepository jpaRepository;

    @InjectMocks
    private BarbeiroRepositoryImpl repository;

    @Test
    void deveSalvarBarbeiroComSucesso() {
        var id = UUID.randomUUID();
        var barbeiro = new Barbeiro(null, "Carlos", "Corte", new BigDecimal("20.00"), true);
        var entity = new BarbeiroEntity(id, "Carlos", "Corte", new BigDecimal("20.00"), true);

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(barbeiro);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Carlos", result.getNome());
        assertEquals("Corte", result.getEspecialidade());
        assertEquals(new BigDecimal("20.00"), result.getPercentualComissao());
        assertTrue(result.isAtivo());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    void deveBuscarBarbeiroPorIdComSucesso() {
        var id = UUID.randomUUID();
        var entity = new BarbeiroEntity(id, "Ana", "Barba", new BigDecimal("15.00"), false);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Ana", result.get().getNome());
        assertFalse(result.get().isAtivo());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoBarbeiroNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveListarBarbeirosComSucesso() {
        var entities = List.of(
                new BarbeiroEntity(UUID.randomUUID(), "Carlos", "Corte", new BigDecimal("20.00"), true),
                new BarbeiroEntity(UUID.randomUUID(), "Ana", "Barba", new BigDecimal("15.00"), false)
        );

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaBarbeiros() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    void deveDeletarBarbeiroComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
