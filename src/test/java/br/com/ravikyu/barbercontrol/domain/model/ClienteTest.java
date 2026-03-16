package br.com.ravikyu.barbercontrol.domain.model;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    @DisplayName("deveCriarClienteComSucesso")
    void deveCriarClienteComSucesso() {
        var id = UUID.randomUUID();
        var nome = Instancio.create(String.class);
        var email = Instancio.gen().net().email().get();
        var telefone = Instancio.create(String.class);

        var cliente = new Cliente(id, nome, email, telefone);

        assertEquals(id, cliente.getId());
        assertEquals(nome, cliente.getNome());
        assertEquals(email, cliente.getEmail());
        assertEquals(telefone, cliente.getTelefone());
        assertNotNull(cliente.getCriadoEm());
    }

    @Test
    @DisplayName("deveCriarClienteSemId")
    void deveCriarClienteSemId() {
        var email = Instancio.gen().net().email().get();

        var cliente = new Cliente(null, Instancio.create(String.class), email, null);

        assertNull(cliente.getId());
    }

    @Test
    @DisplayName("deveCriarClienteSemTelefone")
    void deveCriarClienteSemTelefone() {
        var email = Instancio.gen().net().email().get();

        var cliente = new Cliente(null, Instancio.create(String.class), email, null);

        assertNull(cliente.getTelefone());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoEmailNulo")
    void deveLancarExcecaoQuandoEmailNulo() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Cliente(null, "Ana", null, "99999999999"));

        assertEquals("Email inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveDefinirCriadoEmAutomaticamente")
    void deveDefinirCriadoEmAutomaticamente() {
        var antes = LocalDateTime.now().minusSeconds(1);
        var email = Instancio.gen().net().email().get();

        var cliente = new Cliente(null, Instancio.create(String.class), email, null);
        var depois = LocalDateTime.now().plusSeconds(1);

        assertTrue(cliente.getCriadoEm().isAfter(antes));
        assertTrue(cliente.getCriadoEm().isBefore(depois));
    }

    @Test
    @DisplayName("deveAlterarTelefone")
    void deveAlterarTelefone() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();
        var novoTelefone = Instancio.create(String.class);

        cliente.alterarTelefone(novoTelefone);

        assertEquals(novoTelefone, cliente.getTelefone());
    }

    @Test
    @DisplayName("deveAlterarTelefoneParaNulo")
    void deveAlterarTelefoneParaNulo() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        cliente.alterarTelefone(null);

        assertNull(cliente.getTelefone());
    }

    @Test
    @DisplayName("devePermitirAlterarNome")
    void devePermitirAlterarNome() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();
        var novoNome = Instancio.create(String.class);

        cliente.setNome(novoNome);

        assertEquals(novoNome, cliente.getNome());
    }
}
