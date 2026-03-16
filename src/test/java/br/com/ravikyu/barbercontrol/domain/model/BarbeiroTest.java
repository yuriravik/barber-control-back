package br.com.ravikyu.barbercontrol.domain.model;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class BarbeiroTest {

    private Barbeiro barbeiroValido() {
        return Instancio.of(Barbeiro.class)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    @Test
    @DisplayName("deveCriarBarbeiroComSucesso")
    void deveCriarBarbeiroComSucesso() {
        var nome = Instancio.create(String.class);
        var especialidade = Instancio.create(String.class);
        var percentual = new BigDecimal("20.00");

        var barbeiro = new Barbeiro(null, nome, especialidade, percentual, true);

        assertNull(barbeiro.getId());
        assertEquals(nome, barbeiro.getNome());
        assertEquals(especialidade, barbeiro.getEspecialidade());
        assertEquals(percentual, barbeiro.getPercentualComissao());
        assertTrue(barbeiro.isAtivo());
    }

    @Test
    @DisplayName("deveCriarBarbeiroComComissaoZero")
    void deveCriarBarbeiroComComissaoZero() {
        var barbeiro = new Barbeiro(null, Instancio.create(String.class),
                Instancio.create(String.class), BigDecimal.ZERO, true);

        assertEquals(BigDecimal.ZERO, barbeiro.getPercentualComissao());
    }

    @Test
    @DisplayName("deveCriarBarbeiroComComissaoCem")
    void deveCriarBarbeiroComComissaoCem() {
        var barbeiro = new Barbeiro(null, Instancio.create(String.class),
                Instancio.create(String.class), new BigDecimal("100"), true);

        assertEquals(new BigDecimal("100"), barbeiro.getPercentualComissao());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoComissaoNegativa")
    void deveLancarExcecaoQuandoComissaoNegativa() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Barbeiro(null, "Carlos", "Corte", new BigDecimal("-1"), true));

        assertEquals("Percentual inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoComissaoAcimaDeCem")
    void deveLancarExcecaoQuandoComissaoAcimaDeCem() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Barbeiro(null, "Carlos", "Corte", new BigDecimal("100.01"), true));

        assertEquals("Percentual inválido", ex.getMessage());
    }

    @Test
    @DisplayName("deveCalcularComissaoCorretamente")
    void deveCalcularComissaoCorretamente() {
        var barbeiro = new Barbeiro(null, "Carlos", "Corte", new BigDecimal("20"), true);
        var comissao = barbeiro.calcularComissao(new BigDecimal("100"));

        assertEquals(0, new BigDecimal("20").compareTo(comissao));
    }

    @Test
    @DisplayName("deveCalcularComissaoComValorFracionado")
    void deveCalcularComissaoComValorFracionado() {
        var barbeiro = new Barbeiro(null, "Carlos", "Corte", new BigDecimal("10"), true);
        var comissao = barbeiro.calcularComissao(new BigDecimal("150"));

        assertEquals(0, new BigDecimal("15").compareTo(comissao));
    }

    @Test
    @DisplayName("deveCriarBarbeiroInativo")
    void deveCriarBarbeiroInativo() {
        var barbeiro = barbeiroValido();
        barbeiro.setAtivo(false);

        assertFalse(barbeiro.isAtivo());
    }

    @Test
    @DisplayName("devePermitirAlterarAtivo")
    void devePermitirAlterarAtivo() {
        var barbeiro = barbeiroValido();
        barbeiro.setAtivo(false);

        assertFalse(barbeiro.isAtivo());
    }
}
