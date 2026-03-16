package br.com.ravikyu.barbercontrol.infrastructure.persistence.mapper;

import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteEntityMapperTest {

    @Test
    void deveMapearClienteParaEntity() {
        var id = UUID.randomUUID();
        var cliente = new Cliente(id, "João", "joao@email.com", "11999999999");

        var entity = ClienteEntityMapper.toEntity(cliente);

        assertEquals(id, entity.getId());
        assertEquals("João", entity.getNome());
        assertEquals("11999999999", entity.getTelefone());
        assertEquals("joao@email.com", entity.getEmail());
    }

    @Test
    void deveMapearClienteSemIdParaEntity() {
        var cliente = new Cliente(null, "Maria", "maria@email.com", "21999999999");

        var entity = ClienteEntityMapper.toEntity(cliente);

        assertNull(entity.getId());
        assertEquals("Maria", entity.getNome());
    }

    @Test
    void deveMapearEntityParaClienteResponse() {
        var id = UUID.randomUUID();
        var entity = new ClienteEntity(id, "Pedro", "31999999999", "pedro@email.com");

        var response = ClienteEntityMapper.toDomain(entity);

        assertEquals(id, response.id());
        assertEquals("Pedro", response.nome());
        assertEquals("pedro@email.com", response.email());
        assertEquals("31999999999", response.telefone());
    }

    @Test
    void deveMapearEntitySemTelefoneParaResponse() {
        var id = UUID.randomUUID();
        var entity = new ClienteEntity(id, "Ana", null, "ana@email.com");

        var response = ClienteEntityMapper.toDomain(entity);

        assertNull(response.telefone());
        assertEquals("Ana", response.nome());
    }
}
