package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServicoMapperTest {

    @Test
    void deveMapearRequestParaDomain() {
        var request = new CriarServicoRequest("Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);

        var servico = ServicoMapper.toRequest(request);

        assertNull(servico.getId());
        assertEquals("Corte Simples", servico.getNome());
        assertEquals("Corte básico", servico.getDescricao());
        assertEquals(new BigDecimal("30.00"), servico.getPreco());
        assertEquals(30, servico.getDuracaoMinutos());
        assertTrue(servico.isAtivo());
    }

    @Test
    void deveMapearRequestInativo() {
        var request = new CriarServicoRequest("Barba", "Aparar barba", new BigDecimal("20.00"), 20, false);

        var servico = ServicoMapper.toRequest(request);

        assertFalse(servico.isAtivo());
    }

    @Test
    void deveMapearDomainParaResponse() {
        var id = UUID.randomUUID();
        var servico = new Servico(id, "Corte + Barba", "Combo", new BigDecimal("50.00"), 45, true);

        var response = ServicoMapper.toDomain(servico);

        assertEquals(id, response.id());
        assertEquals("Corte + Barba", response.nome());
        assertEquals("Combo", response.descricao());
        assertEquals(new BigDecimal("50.00"), response.preco());
        assertEquals(45, response.duracaoMinutos());
        assertTrue(response.ativo());
    }

    @Test
    void deveMapearDomainParaResponseInativo() {
        var servico = new Servico(UUID.randomUUID(), "Coloração", "Tingimento", new BigDecimal("80.00"), 60, false);

        var response = ServicoMapper.toDomain(servico);

        assertFalse(response.ativo());
    }
}
