package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoMapperTest {

    @Test
    void deveMapearRequestParaDomain() {
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var dataHora = LocalDateTime.now().plusDays(1);
        var dataHoraFim = dataHora.plusMinutes(30);

        var request = new CriarAgendamentoRequest(clienteId, barbeiroId, servicoId, dataHora, dataHoraFim, "AGENDADO");

        var agendamento = AgendamentoMapper.toDomain(request);

        assertNull(agendamento.getId());
        assertEquals(clienteId, agendamento.getClienteId());
        assertEquals(barbeiroId, agendamento.getBarbeiroId());
        assertEquals(servicoId, agendamento.getServicoId());
        assertEquals(dataHora, agendamento.getDataHoraInicio());
        assertEquals(dataHoraFim, agendamento.getDataHoraFim());
        assertEquals("AGENDADO", agendamento.getStatus());
    }

    @Test
    void deveMapearRequestSemDataHoraFim() {
        var request = new CriarAgendamentoRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1), null, null
        );

        var agendamento = AgendamentoMapper.toDomain(request);

        assertNull(agendamento.getDataHoraFim());
        assertNull(agendamento.getStatus());
    }

    @Test
    void deveMapearRequestComStatusCancelado() {
        var request = new CriarAgendamentoRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1), null, "CANCELADO"
        );

        var agendamento = AgendamentoMapper.toDomain(request);

        assertEquals("CANCELADO", agendamento.getStatus());
    }
}
