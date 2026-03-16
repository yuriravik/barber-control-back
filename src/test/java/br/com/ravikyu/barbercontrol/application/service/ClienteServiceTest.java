package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveCriarClienteComSucesso() {

        // Arrange
        CriarClienteRequest dto = new CriarClienteRequest(
                "Yuri",
                "85999999999",
                "yuri@email.com"
        );

        Cliente clienteSalvo = new Cliente(
                UUID.randomUUID(),
                dto.nome(),
                dto.telefone(),
                dto.email()
        );

        when(repository.salvar(any())).thenReturn(clienteSalvo);

        // Act
        var response = service.criar(dto);

        // Assert
        assertNotNull(response);
        assertEquals("Yuri", response.nome());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    void deveCriarClienteComEmailValido() {
        var dto = new CriarClienteRequest("João", "joao@email.com", "11999999999");
        var clienteSalvo = new Cliente(UUID.randomUUID(), "João", "joao@email.com", "11999999999");

        when(repository.salvar(any())).thenReturn(clienteSalvo);

        var response = service.criar(dto);

        assertNotNull(response);
        assertEquals("João", response.nome());
        assertEquals("joao@email.com", response.email());
        assertEquals("11999999999", response.telefone());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    void deveListarClientesComSucesso() {
        var clientes = List.of(
                new Cliente(UUID.randomUUID(), "Maria", "maria@email.com", "21999999999"),
                new Cliente(UUID.randomUUID(), "Pedro", "pedro@email.com", "31999999999")
        );

        when(repository.listar()).thenReturn(clientes);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Maria", response.get(0).nome());
        assertEquals("Pedro", response.get(1).nome());
        verify(repository, times(1)).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(repository.listar()).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(id, "Ana", "ana@email.com", "41999999999");

        when(repository.buscarPorId(id)).thenReturn(Optional.of(cliente));

        var response = service.buscar(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Ana", response.nome());
        verify(repository, times(1)).buscarPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void deveDeletarClienteComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(repository).deletar(id);

        service.deletar(id);

        verify(repository, times(1)).deletar(id);
    }
}