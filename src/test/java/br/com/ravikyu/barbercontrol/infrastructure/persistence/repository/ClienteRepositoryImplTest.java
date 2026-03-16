package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryImplTest {

    @Mock
    private ClienteJpaRepository jpaRepository;

    @InjectMocks
    private ClienteRepositoryImpl repository;

    @Test
    void deveSalvarClienteComSucesso() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(null, "João", "joao@email.com", "11999999999");
        var entity = new ClienteEntity(id, "João", "11999999999", "joao@email.com");

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(cliente);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("João", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("11999999999", result.getTelefone());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    void deveListarClientesComSucesso() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var entities = List.of(
                new ClienteEntity(id1, "Maria", "21999999999", "maria@email.com"),
                new ClienteEntity(id2, "Pedro", "31999999999", "pedro@email.com")
        );

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        var id = UUID.randomUUID();
        var entity = new ClienteEntity(id, "Ana", "41999999999", "ana@email.com");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Ana", result.get().getNome());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveDeletarClienteComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
