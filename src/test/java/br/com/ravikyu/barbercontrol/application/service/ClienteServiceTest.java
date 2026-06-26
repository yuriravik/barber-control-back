package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.cliente.dto.AtualizarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private ClienteService service;

    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        when(usuarioProvider.getAdminUsuarioIdAutenticado()).thenReturn(usuarioId);
    }

    private Cliente clienteValido() {
        return Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();
    }

    @Test
    @DisplayName("deveCriarClienteComSucesso")
    void deveCriarClienteComSucesso() {
        CriarClienteRequest dto = new CriarClienteRequest(
                "Yuri",
                "yuri@email.com",
                "85999999999"
        );

        Cliente clienteSalvo = Instancio.of(Cliente.class)
                .set(field(Cliente.class, "nome"), dto.nome())
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        when(repository.salvar(any())).thenReturn(clienteSalvo);

        var response = service.criar(dto);

        assertNotNull(response);
        assertEquals("Yuri", response.nome());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("deveCriarClienteComEmailValido")
    void deveCriarClienteComEmailValido() {
        var clienteSalvo = clienteValido();
        var dto = new CriarClienteRequest(
                clienteSalvo.getNome(),
                clienteSalvo.getEmail(),
                clienteSalvo.getTelefone());

        when(repository.salvar(any())).thenReturn(clienteSalvo);

        var response = service.criar(dto);

        assertNotNull(response);
        assertEquals(clienteSalvo.getNome(), response.nome());
        assertEquals(clienteSalvo.getEmail(), response.email());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("deveListarClientesComSucesso")
    void deveListarClientesComSucesso() {
        var clientes = Instancio.ofList(Cliente.class)
                .size(2)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        when(repository.listarPorUsuario(usuarioId)).thenReturn(clientes);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(repository, times(1)).listarPorUsuario(usuarioId);
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaClientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(repository.listarPorUsuario(usuarioId)).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarClientePorIdComSucesso")
    void deveBuscarClientePorIdComSucesso() {
        var cliente = clienteValido();

        when(repository.buscarPorIdEUsuario(cliente.getId(), usuarioId)).thenReturn(Optional.of(cliente));

        var response = service.buscar(cliente.getId());

        assertNotNull(response);
        assertEquals(cliente.getId(), response.id());
        assertEquals(cliente.getNome(), response.nome());
        verify(repository, times(1)).buscarPorIdEUsuario(cliente.getId(), usuarioId);
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoClienteNaoEncontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorIdEUsuario(id, usuarioId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveAtualizarClienteComSucesso")
    void deveAtualizarClienteComSucesso() {
        var cliente = clienteValido();
        var dto = new AtualizarClienteRequest("Nome Atualizado", "novo@email.com", "11977777777");

        when(repository.buscarPorIdEUsuario(cliente.getId(), usuarioId)).thenReturn(Optional.of(cliente));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.atualizar(cliente.getId(), dto);

        assertNotNull(response);
        assertEquals("Nome Atualizado", response.nome());
        assertEquals("novo@email.com", response.email());
        verify(repository).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoAoAtualizarClienteNaoEncontrado")
    void deveLancarExcecaoAoAtualizarClienteNaoEncontrado() {
        var id = UUID.randomUUID();
        var dto = new AtualizarClienteRequest("Nome", "email@test.com", null);

        when(repository.buscarPorIdEUsuario(id, usuarioId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.atualizar(id, dto));

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveDeletarClienteComSucesso")
    void deveDeletarClienteComSucesso() {
        var cliente = clienteValido();

        when(repository.buscarPorIdEUsuario(cliente.getId(), usuarioId)).thenReturn(Optional.of(cliente));
        doNothing().when(repository).deletar(cliente.getId());

        service.deletar(cliente.getId());

        verify(repository, times(1)).deletar(cliente.getId());
    }
}
