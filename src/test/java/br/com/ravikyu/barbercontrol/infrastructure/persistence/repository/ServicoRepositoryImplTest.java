package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ServicoEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoRepositoryImplTest {

    @Mock
    private ServicoJpaRepository jpaRepository;

    @InjectMocks
    private ServicoRepositoryImpl repository;

    @Test
    @DisplayName("deveSalvarServicoComSucesso")
    void deveSalvarServicoComSucesso() {
        var entity = Instancio.create(ServicoEntity.class);
        var servico = Instancio.create(Servico.class);

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(servico);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getNome(), result.getNome());
        assertEquals(entity.isAtivo(), result.isAtivo());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveBuscarServicoPorIdComSucesso")
    void deveBuscarServicoPorIdComSucesso() {
        var entity = Instancio.create(ServicoEntity.class);

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(entity.getNome(), result.get().getNome());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoServicoNaoEncontrado")
    void deveRetornarVazioQuandoServicoNaoEncontrado() {
        var id = Instancio.create(java.util.UUID.class);

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveListarServicosComSucesso")
    void deveListarServicosComSucesso() {
        var entities = Instancio.ofList(ServicoEntity.class).size(2).create();

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaServicos")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveDeletarServicoComSucesso")
    void deveDeletarServicoComSucesso() {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
