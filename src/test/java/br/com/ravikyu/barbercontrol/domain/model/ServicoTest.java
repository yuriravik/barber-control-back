package br.com.ravikyu.barbercontrol.domain.model;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    @Test
    @DisplayName("deveCriarServicoComSucesso")
    void deveCriarServicoComSucesso() {
        var servico = Instancio.create(Servico.class);

        assertNotNull(servico.getId());
        assertNotNull(servico.getNome());
        assertNotNull(servico.getDescricao());
        assertNotNull(servico.getPreco());
        assertNotNull(servico.getDuracaoMinutos());
    }

    @Test
    @DisplayName("deveCriarServicoInativo")
    void deveCriarServicoInativo() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), false)
                .create();

        assertFalse(servico.isAtivo());
    }

    @Test
    @DisplayName("deveCriarServicoSemId")
    void deveCriarServicoSemId() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "id"), null)
                .create();

        assertNull(servico.getId());
    }

    @Test
    @DisplayName("devePermitirAlterarAtivo")
    void devePermitirAlterarAtivo() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), true)
                .create();

        servico.setAtivo(false);

        assertFalse(servico.isAtivo());
    }

    @Test
    @DisplayName("devePermitirAlterarPreco")
    void devePermitirAlterarPreco() {
        var servico = Instancio.create(Servico.class);
        var novoPreco = Instancio.gen().math().bigDecimal().scale(2).min(java.math.BigDecimal.ONE).get();

        servico.setPreco(novoPreco);

        assertEquals(novoPreco, servico.getPreco());
    }

    @Test
    @DisplayName("devePermitirAlterarDuracao")
    void devePermitirAlterarDuracao() {
        var servico = Instancio.create(Servico.class);
        var novaDuracao = Instancio.gen().ints().range(10, 120).get();

        servico.setDuracaoMinutos(novaDuracao);

        assertEquals(novaDuracao, servico.getDuracaoMinutos());
    }
}
