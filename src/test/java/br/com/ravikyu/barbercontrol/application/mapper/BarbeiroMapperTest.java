package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class BarbeiroMapperTest {

    private CriarBarbeiroRequest requestValido() {
        return Instancio.of(CriarBarbeiroRequest.class)
                .generate(field(CriarBarbeiroRequest.class, "nome"), gen -> gen.string().minLength(1))
                .generate(field(CriarBarbeiroRequest.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    @Test
    @DisplayName("deveMapearRequestParaDomain")
    void deveMapearRequestParaDomain() {
        var request = requestValido();

        var barbeiro = BarbeiroMapper.toDomain(request);

        assertNull(barbeiro.getId());
        assertEquals(request.nome(), barbeiro.getNome());
        assertEquals(request.especialidade(), barbeiro.getEspecialidade());
        assertEquals(request.percentualComissao(), barbeiro.getPercentualComissao());
        assertEquals(request.ativo(), barbeiro.isAtivo());
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainInativo")
    void deveMapearRequestParaDomainInativo() {
        var request = Instancio.of(CriarBarbeiroRequest.class)
                .generate(field(CriarBarbeiroRequest.class, "nome"), gen -> gen.string().minLength(1))
                .generate(field(CriarBarbeiroRequest.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .set(field(CriarBarbeiroRequest.class, "ativo"), false)
                .create();

        var barbeiro = BarbeiroMapper.toDomain(request);

        assertFalse(barbeiro.isAtivo());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponse")
    void deveMapearDomainParaResponse() {
        var barbeiro = Instancio.of(Barbeiro.class)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();

        var response = BarbeiroMapper.toResponse(barbeiro);

        assertEquals(barbeiro.getId(), response.id());
        assertEquals(barbeiro.getNome(), response.nome());
        assertEquals(barbeiro.getEspecialidade(), response.especialidade());
        assertEquals(barbeiro.getPercentualComissao(), response.percentualComissao());
        assertEquals(barbeiro.isAtivo(), response.ativo());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponseComAtivoPadrao")
    void deveMapearDomainParaResponseComAtivoPadrao() {
        var barbeiro = Instancio.of(Barbeiro.class)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .set(field(Barbeiro.class, "ativo"), false)
                .create();

        var response = BarbeiroMapper.toResponse(barbeiro);

        assertFalse(response.ativo());
    }
}
