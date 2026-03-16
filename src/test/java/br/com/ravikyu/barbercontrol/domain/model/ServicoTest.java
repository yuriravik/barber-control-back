package br.com.ravikyu.barbercontrol.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    @Test
    void deveCriarServicoComSucesso() {
        var id = UUID.randomUUID();
        var servico = new Servico(id, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);

        assertEquals(id, servico.getId());
        assertEquals("Corte Simples", servico.getNome());
        assertEquals("Corte básico", servico.getDescricao());
        assertEquals(new BigDecimal("30.00"), servico.getPreco());
        assertEquals(30, servico.getDuracaoMinutos());
        assertTrue(servico.isAtivo());
    }

    @Test
    void deveCriarServicoInativo() {
        var servico = new Servico(null, "Barba", "Aparar barba", new BigDecimal("20.00"), 20, false);

        assertFalse(servico.isAtivo());
    }

    @Test
    void deveCriarServicoSemId() {
        var servico = new Servico(null, "Coloração", "Tingimento", new BigDecimal("80.00"), 60, true);

        assertNull(servico.getId());
    }

    @Test
    void devePermitirAlterarAtivo() {
        var servico = new Servico(null, "Corte", "Desc", new BigDecimal("25.00"), 25, true);
        servico.setAtivo(false);

        assertFalse(servico.isAtivo());
    }

    @Test
    void devePermitirAlterarPreco() {
        var servico = new Servico(null, "Corte", "Desc", new BigDecimal("25.00"), 25, true);
        servico.setPreco(new BigDecimal("35.00"));

        assertEquals(new BigDecimal("35.00"), servico.getPreco());
    }

    @Test
    void devePermitirAlterarDuracao() {
        var servico = new Servico(null, "Corte", "Desc", new BigDecimal("25.00"), 25, true);
        servico.setDuracaoMinutos(45);

        assertEquals(45, servico.getDuracaoMinutos());
    }
}
