package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BarbeiroMapperTest {

    @Test
    void deveMapearRequestParaDomain() {
        var request = new CriarBarbeiroRequest("Carlos", "Corte", new BigDecimal("20.00"), true);

        var barbeiro = BarbeiroMapper.toDomain(request);

        assertNull(barbeiro.getId());
        assertEquals("Carlos", barbeiro.getNome());
        assertEquals("Corte", barbeiro.getEspecialidade());
        assertEquals(new BigDecimal("20.00"), barbeiro.getPercentualComissao());
        assertTrue(barbeiro.isAtivo());
    }

    @Test
    void deveMapearRequestParaDomainInativo() {
        var request = new CriarBarbeiroRequest("Ana", "Barba", new BigDecimal("15.00"), false);

        var barbeiro = BarbeiroMapper.toDomain(request);

        assertFalse(barbeiro.isAtivo());
    }

    @Test
    void deveMapearDomainParaResponse() {
        var id = UUID.randomUUID();
        var request = new CriarBarbeiroRequest("José", "Sobrancelha", new BigDecimal("25.00"), true);
        var barbeiro = BarbeiroMapper.toDomain(request);
        barbeiro.setId(id);

        var response = BarbeiroMapper.toResponse(barbeiro);

        assertEquals(id, response.id());
        assertEquals("José", response.nome());
        assertEquals("Sobrancelha", response.especialidade());
        assertEquals(new BigDecimal("25.00"), response.percentualComissao());
        assertTrue(response.ativo());
    }

    @Test
    void deveMapearDomainParaResponseComAtivoPadrao() {
        var request = new CriarBarbeiroRequest("Paulo", "Barba", new BigDecimal("10.00"), false);
        var barbeiro = BarbeiroMapper.toDomain(request);

        var response = BarbeiroMapper.toResponse(barbeiro);

        assertFalse(response.ativo());
    }
}
