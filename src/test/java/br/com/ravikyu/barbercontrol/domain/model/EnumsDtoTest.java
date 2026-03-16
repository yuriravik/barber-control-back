package br.com.ravikyu.barbercontrol.domain.model;

import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginRequest;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusPagamento;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumsDtoTest {

    @Test
    @DisplayName("deveConterTodosOsStatusAgendamento")
    void deveConterTodosOsStatusAgendamento() {
        var values = StatusAgendamento.values();

        assertEquals(3, values.length);
        assertEquals(StatusAgendamento.AGENDADO, StatusAgendamento.valueOf("AGENDADO"));
        assertEquals(StatusAgendamento.CANCELADO, StatusAgendamento.valueOf("CANCELADO"));
        assertEquals(StatusAgendamento.CONCLUIDO, StatusAgendamento.valueOf("CONCLUIDO"));
    }

    @Test
    @DisplayName("deveConterTodosOsStatusPagamento")
    void deveConterTodosOsStatusPagamento() {
        var values = StatusPagamento.values();

        assertEquals(3, values.length);
        assertEquals(StatusPagamento.PENDENTE, StatusPagamento.valueOf("PENDENTE"));
        assertEquals(StatusPagamento.PAGO, StatusPagamento.valueOf("PAGO"));
        assertEquals(StatusPagamento.CANCELADO, StatusPagamento.valueOf("CANCELADO"));
    }

    @Test
    @DisplayName("deveConterTodasAsFormasDePagamento")
    void deveConterTodasAsFormasDePagamento() {
        var values = FormaPagamento.values();

        assertEquals(3, values.length);
        assertEquals(FormaPagamento.PIX, FormaPagamento.valueOf("PIX"));
        assertEquals(FormaPagamento.CARTAO, FormaPagamento.valueOf("CARTAO"));
        assertEquals(FormaPagamento.DINHEIRO, FormaPagamento.valueOf("DINHEIRO"));
    }

    @Test
    @DisplayName("deveConterTodasAsRoles")
    void deveConterTodasAsRoles() {
        var values = Role.values();

        assertEquals(2, values.length);
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.BARBEIRO, Role.valueOf("BARBEIRO"));
    }

    @Test
    @DisplayName("deveCriarLoginRequestComSucesso")
    void deveCriarLoginRequestComSucesso() {
        var email = Instancio.gen().net().email().get();
        var senha = Instancio.create(String.class);

        var request = new LoginRequest(email, senha);

        assertEquals(email, request.email());
        assertEquals(senha, request.senha());
    }
}
