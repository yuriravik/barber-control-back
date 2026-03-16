package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
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
class ClienteRepositoryImplTest {

    @Mock
    private ClienteJpaRepository jpaRepository;

    @InjectMocks
    private ClienteRepositoryImpl repository;

    private ClienteEntity entidadeValida() {
        return Instancio.of(ClienteEntity.class)
                .generate(field(ClienteEntity.class, "email"), gen -> gen.net().email())
                .create();
    }

    @Test
    @DisplayName("deveSalvarClienteComSucesso")
    void deveSalvarClienteComSucesso() {
        var entity = entidadeValida();
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(cliente);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getNome(), result.getNome());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveListarClientesComSucesso")
    void deveListarClientesComSucesso() {
        var entities = Instancio.ofList(ClienteEntity.class).size(2)
                .generate(field(ClienteEntity.class, "email"), gen -> gen.net().email())
                .create();

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaClientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarClientePorIdComSucesso")
    void deveBuscarClientePorIdComSucesso() {
        var entity = entidadeValida();

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(entity.getNome(), result.get().getNome());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoClienteNaoEncontrado")
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        var id = Instancio.create(java.util.UUID.class);

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveDeletarClienteComSucesso")
    void deveDeletarClienteComSucesso() {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
