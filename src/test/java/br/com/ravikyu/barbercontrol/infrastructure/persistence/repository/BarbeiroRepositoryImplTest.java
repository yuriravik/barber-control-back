package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.BarbeiroEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarbeiroRepositoryImplTest {

    @Mock
    private BarbeiroJpaRepository jpaRepository;

    @InjectMocks
    private BarbeiroRepositoryImpl repository;

    private BarbeiroEntity entidadeValida() {
        return Instancio.of(BarbeiroEntity.class)
                .generate(field(BarbeiroEntity.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    @Test
    @DisplayName("deveSalvarBarbeiroComSucesso")
    void deveSalvarBarbeiroComSucesso() {
        var entity = entidadeValida();
        var barbeiro = Instancio.of(br.com.ravikyu.barbercontrol.domain.model.Barbeiro.class)
                .generate(field(br.com.ravikyu.barbercontrol.domain.model.Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(barbeiro);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getNome(), result.getNome());
        assertEquals(entity.getPercentualComissao(), result.getPercentualComissao());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveBuscarBarbeiroPorIdComSucesso")
    void deveBuscarBarbeiroPorIdComSucesso() {
        var entity = entidadeValida();

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(entity.isAtivo(), result.get().isAtivo());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoBarbeiroNaoEncontrado")
    void deveRetornarVazioQuandoBarbeiroNaoEncontrado() {
        var id = Instancio.create(java.util.UUID.class);

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveListarBarbeirosComSucesso")
    void deveListarBarbeirosComSucesso() {
        var entities = Instancio.ofList(BarbeiroEntity.class).size(2)
                .generate(field(BarbeiroEntity.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaBarbeiros")
    void deveRetornarListaVaziaQuandoNaoHaBarbeiros() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveDeletarBarbeiroComSucesso")
    void deveDeletarBarbeiroComSucesso() {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
