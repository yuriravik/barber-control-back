package br.com.ravikyu.barbercontrol.domain.model;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

class AgendamentoTest {

    @Test
    @DisplayName("deveCriarAgendamentoComSucesso")
    void deveCriarAgendamentoComSucesso() {
        var agendamento = Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "status"), "AGENDADO")
                .create();

        assertNotNull(agendamento.getId());
        assertNotNull(agendamento.getClienteId());
        assertNotNull(agendamento.getBarbeiroId());
        assertNotNull(agendamento.getServicoId());
        assertNotNull(agendamento.getDataHoraInicio());
        assertEquals("AGENDADO", agendamento.getStatus());
    }

    @Test
    @DisplayName("deveCriarAgendamentoVazioComConstrutorPadrao")
    void deveCriarAgendamentoVazioComConstrutorPadrao() {
        var agendamento = new Agendamento();

        assertNull(agendamento.getId());
        assertNull(agendamento.getClienteId());
        assertNull(agendamento.getStatus());
    }

    @Test
    @DisplayName("devePermitirAlterarStatus")
    void devePermitirAlterarStatus() {
        var agendamento = Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "status"), "AGENDADO")
                .create();

        agendamento.setStatus("CANCELADO");

        assertEquals("CANCELADO", agendamento.getStatus());
    }

    @Test
    @DisplayName("devePermitirAlterarDataHoraFim")
    void devePermitirAlterarDataHoraFim() {
        var agendamento = Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "dataHoraFim"), null)
                .create();
        var novaDataFim = agendamento.getDataHoraInicio().plusMinutes(45);

        agendamento.setDataHoraFim(novaDataFim);

        assertEquals(novaDataFim, agendamento.getDataHoraFim());
    }

    @Test
    @DisplayName("devePermitirCriarComStatusCancelado")
    void devePermitirCriarComStatusCancelado() {
        var agendamento = Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "status"), "CANCELADO")
                .create();

        assertEquals("CANCELADO", agendamento.getStatus());
    }
}
