package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.mapper.ClienteMapper;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    @Test
    @DisplayName("deveMapearRequestParaDomain")
    void deveMapearRequestParaDomain() {
        var request = Instancio.of(CriarClienteRequest.class)
                .generate(field(CriarClienteRequest.class, "nome"), gen -> gen.string().minLength(1))
                .generate(field(CriarClienteRequest.class, "email"), gen -> gen.net().email())
                .create();

        var cliente = ClienteMapper.toDomain(request);

        assertNull(cliente.getId());
        assertEquals(request.nome(), cliente.getNome());
        assertEquals(request.email(), cliente.getEmail());
        assertEquals(request.telefone(), cliente.getTelefone());
    }

    @Test
    @DisplayName("deveMapearRequestParaDomainSemTelefone")
    void deveMapearRequestParaDomainSemTelefone() {
        var request = Instancio.of(CriarClienteRequest.class)
                .generate(field(CriarClienteRequest.class, "nome"), gen -> gen.string().minLength(1))
                .generate(field(CriarClienteRequest.class, "email"), gen -> gen.net().email())
                .set(field(CriarClienteRequest.class, "telefone"), null)
                .create();

        var cliente = ClienteMapper.toDomain(request);

        assertEquals(request.nome(), cliente.getNome());
        assertNull(cliente.getTelefone());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponse")
    void deveMapearDomainParaResponse() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        var response = ClienteMapper.toResponse(cliente);

        assertEquals(cliente.getId(), response.id());
        assertEquals(cliente.getNome(), response.nome());
        assertEquals(cliente.getEmail(), response.email());
        assertEquals(cliente.getTelefone(), response.telefone());
    }

    @Test
    @DisplayName("deveMapearDomainParaResponseSemTelefone")
    void deveMapearDomainParaResponseSemTelefone() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .set(field(Cliente.class, "telefone"), null)
                .create();

        var response = ClienteMapper.toResponse(cliente);

        assertEquals(cliente.getId(), response.id());
        assertNull(response.telefone());
    }
}
