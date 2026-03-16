package br.com.ravikyu.barbercontrol.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteComSucesso() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(id, "João Silva", "joao@email.com", "11999999999");

        assertEquals(id, cliente.getId());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("11999999999", cliente.getTelefone());
        assertNotNull(cliente.getCriadoEm());
    }

    @Test
    void deveCriarClienteSemId() {
        var cliente = new Cliente(null, "Maria", "maria@email.com", "21999999999");

        assertNull(cliente.getId());
        assertEquals("Maria", cliente.getNome());
    }

    @Test
    void deveCriarClienteSemTelefone() {
        var cliente = new Cliente(null, "Pedro", "pedro@email.com", null);

        assertNull(cliente.getTelefone());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNulo() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Cliente(null, "Ana", null, "99999999999"));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    void deveDefinirCriadoEmAutomaticamente() {
        var antes = LocalDateTime.now().minusSeconds(1);
        var cliente = new Cliente(null, "Teste", "teste@email.com", null);
        var depois = LocalDateTime.now().plusSeconds(1);

        assertTrue(cliente.getCriadoEm().isAfter(antes));
        assertTrue(cliente.getCriadoEm().isBefore(depois));
    }

    @Test
    void deveAlterarTelefone() {
        var cliente = new Cliente(null, "Carlos", "carlos@email.com", "11111111111");
        cliente.alterarTelefone("22222222222");

        assertEquals("22222222222", cliente.getTelefone());
    }

    @Test
    void deveAlterarTelefoneParaNulo() {
        var cliente = new Cliente(null, "Carlos", "carlos@email.com", "11111111111");
        cliente.alterarTelefone(null);

        assertNull(cliente.getTelefone());
    }

    @Test
    void devePermitirAlterarNome() {
        var cliente = new Cliente(null, "NomeAntigo", "email@email.com", null);
        cliente.setNome("NomeNovo");

        assertEquals("NomeNovo", cliente.getNome());
    }
}
