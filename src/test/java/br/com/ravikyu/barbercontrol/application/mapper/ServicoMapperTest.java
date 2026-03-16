package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class ServicoMapperTest {

    @Test
    @DisplayName("deveMapearRequestParaDomain")
    void deveMapearRequestParaDomain() {
        var request = Instancio.of(CriarServicoRequest.class)
                .generate(field(CriarServicoRequest.class, "nome"), gen -> gen.string().minLength(1))
                .create();

        var servico = ServicoMapper.toRequest(request);

        assertNull(servico.getId());
        assertEquals(request.nome(), servico.getNome());
        assertEquals(request.descricao(), servico.getDescricao());
        assertEquals(request.preco(), servico.getPreco());
        assertEquals(request.duracaoMinutos(), servico.getDuracaoMinutos());
        assertEquals(request.ativo(), servico.isAtivo());
    }

    @Test
    @DisplayName("deveMapearRequestInativo")
    void deveMapearRequestInativo() {
        var request = Instancio.of(CriarServicoRequest.class)
                .generate(field(CriarServicoRequest.class, "nome"), gen -> gen.string().minLength(1))
                .set(field(CriarServicoRequest.class, "ativo"), false)
                .create();

        var servico = ServicoMapper.toRequest(request);

        assertFalse(servico.isAtivo());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponse")
    void deveMapearDomainParaResponse() {
        var servico = Instancio.create(Servico.class);

        var response = ServicoMapper.toDomain(servico);

        assertEquals(servico.getId(), response.id());
        assertEquals(servico.getNome(), response.nome());
        assertEquals(servico.getDescricao(), response.descricao());
        assertEquals(servico.getPreco(), response.preco());
        assertEquals(servico.getDuracaoMinutos(), response.duracaoMinutos());
        assertEquals(servico.isAtivo(), response.ativo());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponseInativo")
    void deveMapearDomainParaResponseInativo() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), false)
                .create();

        var response = ServicoMapper.toDomain(servico);

        assertFalse(response.ativo());
    }
}
