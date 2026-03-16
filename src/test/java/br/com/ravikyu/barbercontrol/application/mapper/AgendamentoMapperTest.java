package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.agendamento.dto.CriarAgendamentoRequest;
import br.com.ravikyu.barbercontrol.application.agendamento.mapper.AgendamentoMapper;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
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
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertNull(agendamento.getId());
        assertEquals(request.clienteId(), agendamento.getClienteId());
        assertEquals(request.barbeiroId(), agendamento.getBarbeiroId());
        assertEquals(request.servicoId(), agendamento.getServicoId());
        assertEquals(dataHora, agendamento.getDataHoraInicio());
        assertNull(agendamento.getDataHoraFim());
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("deveMapearRequestSempreComStatusAgendado")
    void deveMapearRequestSempreComStatusAgendado() {
        var request = Instancio.of(CriarAgendamentoRequest.class)
                .set(field(CriarAgendamentoRequest.class, "dataHora"), LocalDateTime.now().plusDays(1))
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
        assertNull(agendamento.getDataHoraFim());
    }

    @Test
    @DisplayName("deveMapearRequestPreservandoDataHoraInicio")
    void deveMapearRequestPreservandoDataHoraInicio() {
        var dataHora = LocalDateTime.now().plusDays(2);
        var request = Instancio.of(CriarAgendamentoRequest.class)
                .set(field(CriarAgendamentoRequest.class, "dataHora"), dataHora)
                .create();

        var agendamento = AgendamentoMapper.toDomain(request);

        assertEquals(dataHora, agendamento.getDataHoraInicio());
    }
}
