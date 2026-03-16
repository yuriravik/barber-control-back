package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.mapper.ClienteMapper;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    @Test
    void deveMapearRequestParaDomain() {
        var request = new CriarClienteRequest("João", "joao@email.com", "11999999999");

        var cliente = ClienteMapper.toDomain(request);

        assertNull(cliente.getId());
        assertEquals("João", cliente.getNome());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("11999999999", cliente.getTelefone());
    }

    @Test
    void deveMapearRequestParaDomainSemTelefone() {
        var request = new CriarClienteRequest("Maria", "maria@email.com", null);

        var cliente = ClienteMapper.toDomain(request);

        assertEquals("Maria", cliente.getNome());
        assertNull(cliente.getTelefone());
    }

    @Test
    void deveMapearDomainParaResponse() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(id, "Pedro", "pedro@email.com", "21999999999");

        var response = ClienteMapper.toResponse(cliente);

        assertEquals(id, response.id());
        assertEquals("Pedro", response.nome());
        assertEquals("pedro@email.com", response.email());
        assertEquals("21999999999", response.telefone());
    }

    @Test
    void deveMapearDomainParaResponseSemTelefone() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(id, "Ana", "ana@email.com", null);

        var response = ClienteMapper.toResponse(cliente);

        assertEquals(id, response.id());
        assertNull(response.telefone());
    }
}
