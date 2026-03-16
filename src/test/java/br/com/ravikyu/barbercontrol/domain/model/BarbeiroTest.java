package br.com.ravikyu.barbercontrol.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BarbeiroTest {

    @Test
    void deveCriarBarbeiroComSucesso() {
        var id = UUID.randomUUID();
        var barbeiro = new Barbeiro(id, "Carlos", "Corte", new BigDecimal("20.00"), true);

        assertEquals(id, barbeiro.getId());
        assertEquals("Carlos", barbeiro.getNome());
        assertEquals("Corte", barbeiro.getEspecialidade());
        assertEquals(new BigDecimal("20.00"), barbeiro.getPercentualComissao());
        assertTrue(barbeiro.isAtivo());
    }

    @Test
    void deveCriarBarbeiroComComissaoZero() {
        var barbeiro = new Barbeiro(null, "Ana", "Barba", BigDecimal.ZERO, true);

        assertEquals(BigDecimal.ZERO, barbeiro.getPercentualComissao());
    }

    @Test
    void deveCriarBarbeiroComComissaoCem() {
        var barbeiro = new Barbeiro(null, "Bob", "Sobrancelha", new BigDecimal("100"), true);

        assertEquals(new BigDecimal("100"), barbeiro.getPercentualComissao());
    }

    @Test
    void deveLancarExcecaoQuandoComissaoNegativa() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Barbeiro(null, "Carlos", "Corte", new BigDecimal("-1"), true));

        assertEquals("Percentual inválido", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoComissaoAcimaDeCem() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Barbeiro(null, "Carlos", "Corte", new BigDecimal("100.01"), true));

        assertEquals("Percentual inválido", ex.getMessage());
    }

    @Test
    void deveCalcularComissaoCorretamente() {
        var barbeiro = new Barbeiro(null, "Carlos", "Corte", new BigDecimal("20"), true);
        var comissao = barbeiro.calcularComissao(new BigDecimal("100"));

        assertEquals(0, new BigDecimal("20").compareTo(comissao));
    }

    @Test
    void deveCalcularComissaoComValorFracionado() {
        var barbeiro = new Barbeiro(null, "Carlos", "Corte", new BigDecimal("10"), true);
        var comissao = barbeiro.calcularComissao(new BigDecimal("150"));

        assertEquals(0, new BigDecimal("15").compareTo(comissao));
    }

    @Test
    void deveCriarBarbeiroInativo() {
        var barbeiro = new Barbeiro(null, "José", "Corte", new BigDecimal("15"), false);

        assertFalse(barbeiro.isAtivo());
    }

    @Test
    void devePermitirAlterarAtivo() {
        var barbeiro = new Barbeiro(null, "José", "Corte", new BigDecimal("15"), true);
        barbeiro.setAtivo(false);

        assertFalse(barbeiro.isAtivo());
    }
}
