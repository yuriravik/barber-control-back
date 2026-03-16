package br.com.ravikyu.barbercontrol.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoTest {

    @Test
    void deveCriarAgendamentoComSucesso() {
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();
        var inicio = LocalDateTime.now().plusHours(1);
        var fim = inicio.plusMinutes(30);

        var agendamento = new Agendamento(id, clienteId, barbeiroId, servicoId, inicio, fim, "AGENDADO");

        assertEquals(id, agendamento.getId());
        assertEquals(clienteId, agendamento.getClienteId());
        assertEquals(barbeiroId, agendamento.getBarbeiroId());
        assertEquals(servicoId, agendamento.getServicoId());
        assertEquals(inicio, agendamento.getDataHoraInicio());
        assertEquals(fim, agendamento.getDataHoraFim());
        assertEquals("AGENDADO", agendamento.getStatus());
    }

    @Test
    void deveCriarAgendamentoVazioComConstrutorPadrao() {
        var agendamento = new Agendamento();

        assertNull(agendamento.getId());
        assertNull(agendamento.getClienteId());
        assertNull(agendamento.getStatus());
    }

    @Test
    void devePermitirAlterarStatus() {
        var agendamento = new Agendamento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), "AGENDADO"
        );

        agendamento.setStatus("CANCELADO");

        assertEquals("CANCELADO", agendamento.getStatus());
    }

    @Test
    void devePermitirAlterarDataHoraFim() {
        var inicio = LocalDateTime.now().plusHours(1);
        var agendamento = new Agendamento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                inicio, null, "AGENDADO"
        );

        var fim = inicio.plusMinutes(45);
        agendamento.setDataHoraFim(fim);

        assertEquals(fim, agendamento.getDataHoraFim());
    }

    @Test
    void devePermitirCriarComStatusCancelado() {
        var agendamento = new Agendamento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), "CANCELADO"
        );

        assertEquals("CANCELADO", agendamento.getStatus());
    }
}
