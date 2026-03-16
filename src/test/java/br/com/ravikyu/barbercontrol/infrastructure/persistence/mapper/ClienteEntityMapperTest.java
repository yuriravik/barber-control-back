package br.com.ravikyu.barbercontrol.infrastructure.persistence.mapper;

import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.ClienteEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class ClienteEntityMapperTest {

    @Test
    @DisplayName("deveMapearClienteParaEntity")
    void deveMapearClienteParaEntity() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .create();

        var entity = ClienteEntityMapper.toEntity(cliente);

        assertEquals(cliente.getId(), entity.getId());
        assertEquals(cliente.getNome(), entity.getNome());
        assertEquals(cliente.getTelefone(), entity.getTelefone());
        assertEquals(cliente.getEmail(), entity.getEmail());
    }

    @Test
    @DisplayName("deveMapearClienteSemIdParaEntity")
    void deveMapearClienteSemIdParaEntity() {
        var cliente = Instancio.of(Cliente.class)
                .generate(field(Cliente.class, "email"), gen -> gen.net().email())
                .set(field(Cliente.class, "id"), null)
                .create();

        var entity = ClienteEntityMapper.toEntity(cliente);

        assertNull(entity.getId());
        assertEquals(cliente.getNome(), entity.getNome());
    }

    @Test
    @DisplayName("deveMapearEntityParaClienteResponse")
    void deveMapearEntityParaClienteResponse() {
        var entity = Instancio.of(ClienteEntity.class)
                .generate(field(ClienteEntity.class, "email"), gen -> gen.net().email())
                .create();

        var response = ClienteEntityMapper.toDomain(entity);

        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getNome(), response.nome());
        assertEquals(entity.getEmail(), response.email());
        assertEquals(entity.getTelefone(), response.telefone());
    }

    @Test
    @DisplayName("deveMapearEntitySemTelefoneParaResponse")
    void deveMapearEntitySemTelefoneParaResponse() {
        var entity = Instancio.of(ClienteEntity.class)
                .generate(field(ClienteEntity.class, "email"), gen -> gen.net().email())
                .set(field(ClienteEntity.class, "telefone"), null)
                .create();

        var response = ClienteEntityMapper.toDomain(entity);

        assertNull(response.telefone());
        assertEquals(entity.getNome(), response.nome());
    }
}
