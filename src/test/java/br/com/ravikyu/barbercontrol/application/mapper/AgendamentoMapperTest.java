package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.CriarAgendamentoRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class AgendamentoMapperTest {

    @Test
    @DisplayName("deveMapearRequestParaDomain")
    void deveMapearRequestParaDomain() {
        var dataHora = LocalDateTime.now().plusDays(1);
        var request = Instancio.of(CriarAgendamentoRequest.class)
                .set(field(CriarAgendamentoRequest.class, "dataHora"), dataHora)
                .set(field(CriarAgendamentoRequest.class, "dataHoraFim"), dataHora.plusMinutes(30))
                .set(field(CriarAgendamentoRequest.class, "status"), "AGENDADO")
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertNull(agendamento.getId());
        assertEquals(request.clienteId(), agendamento.getClienteId());
        assertEquals(request.barbeiroId(), agendamento.getBarbeiroId());
        assertEquals(request.servicoId(), agendamento.getServicoId());
        assertEquals(dataHora, agendamento.getDataHoraInicio());
        assertEquals(request.dataHoraFim(), agendamento.getDataHoraFim());
        assertEquals("AGENDADO", agendamento.getStatus());
    }

    @Test
    @DisplayName("deveMapearRequestSemDataHoraFim")
    void deveMapearRequestSemDataHoraFim() {
        var request = Instancio.of(CriarAgendamentoRequest.class)
                .set(field(CriarAgendamentoRequest.class, "dataHora"), LocalDateTime.now().plusDays(1))
                .set(field(CriarAgendamentoRequest.class, "dataHoraFim"), null)
                .set(field(CriarAgendamentoRequest.class, "status"), null)
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertNull(agendamento.getDataHoraFim());
        assertNull(agendamento.getStatus());
    }

    @Test
    @DisplayName("deveMapearRequestComStatusCancelado")
    void deveMapearRequestComStatusCancelado() {
        var request = Instancio.of(CriarAgendamentoRequest.class)
                .set(field(CriarAgendamentoRequest.class, "dataHora"), LocalDateTime.now().plusDays(1))
                .set(field(CriarAgendamentoRequest.class, "status"), "CANCELADO")
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertEquals("CANCELADO", agendamento.getStatus());
    }
}
