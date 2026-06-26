package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.pagamento.service.PagamentoService;
import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.model.Pagamento;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.FormaPagamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.domain.repository.AgendamentoRepository;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.domain.repository.PagamentoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository repository;
    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private BarbeiroRepository barbeiroRepository;
    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private PagamentoService service;

    @Test
    @DisplayName("deveListarPagamentosDoTenantDoAdmin")
    void deveListarPagamentosDoTenantDoAdmin() {
        var adminId = UUID.randomUUID();
        var admin = usuario(adminId, "admin@barber.com", Role.ADMIN, null, null);
        var barbeiroId = UUID.randomUUID();
        var barbeiro = barbeiro(barbeiroId, adminId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamento = pagamento(agendamento.getId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(admin);
        when(barbeiroRepository.listarPorUsuario(adminId)).thenReturn(List.of(barbeiro));
        when(agendamentoRepository.listarPorBarbeiroIds(List.of(barbeiroId))).thenReturn(List.of(agendamento));
        when(repository.listarComFiltros(List.of(agendamento.getId()), null, null)).thenReturn(List.of(pagamento));

        var response = service.listar();

        assertEquals(1, response.size());
        assertEquals(pagamento.getId(), response.get(0).id());
    }

    @Test
    @DisplayName("deveRetornarListaVaziaParaSecretariaSemAdminVinculado")
    void deveRetornarListaVaziaParaSecretariaSemAdminVinculado() {
        var secretaria = usuario(UUID.randomUUID(), "sec@barber.com", Role.SECRETARIA, null, null);

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(secretaria);
        when(repository.listarComFiltros(List.of(), null, null)).thenReturn(List.of());

        var response = service.listar();

        assertTrue(response.isEmpty());
        verify(repository).listarComFiltros(List.of(), null, null);
    }

    @Test
    @DisplayName("deveLancarNotFoundAoBuscarPagamentoDeOutroTenant")
    void deveLancarNotFoundAoBuscarPagamentoDeOutroTenant() {
        var adminIdTenantA = UUID.randomUUID();
        var adminIdTenantB = UUID.randomUUID();
        var adminTenantB = usuario(adminIdTenantB, "admin-b@barber.com", Role.ADMIN, null, null);
        var barbeiroTenantA = barbeiro(UUID.randomUUID(), adminIdTenantA);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroTenantA.getId());
        var pagamento = pagamento(agendamento.getId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(adminTenantB);
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(agendamentoRepository.buscarPorId(agendamento.getId())).thenReturn(Optional.of(agendamento));
        when(barbeiroRepository.buscarPorId(barbeiroTenantA.getId())).thenReturn(Optional.of(barbeiroTenantA));

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(pagamento.getId()));

        assertEquals("Pagamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("devePermitirBarbeiroBuscarPagamentoDoProprioAgendamento")
    void devePermitirBarbeiroBuscarPagamentoDoProprioAgendamento() {
        var barbeiroId = UUID.randomUUID();
        var barbeiroUsuario = usuario(UUID.randomUUID(), "barbeiro@barber.com", Role.BARBEIRO, UUID.randomUUID(), barbeiroId);
        var agendamento = agendamento(UUID.randomUUID(), barbeiroId);
        var pagamento = pagamento(agendamento.getId());

        when(usuarioProvider.getUsuarioAutenticado()).thenReturn(barbeiroUsuario);
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(agendamentoRepository.buscarPorId(agendamento.getId())).thenReturn(Optional.of(agendamento));

        var response = service.buscar(pagamento.getId());

        assertEquals(pagamento.getId(), response.id());
        assertEquals("PIX", response.formaPagamento());
    }

    private Usuario usuario(UUID id, String email, Role role, UUID adminId, UUID barbeiroId) {
        var usuario = new Usuario(email, "senha123", role);
        usuario.setId(id);
        usuario.setAdminId(adminId);
        usuario.setBarbeiroId(barbeiroId);
        return usuario;
    }

    private Barbeiro barbeiro(UUID id, UUID usuarioId) {
        var barbeiro = new Barbeiro(id, "Barbeiro", "Corte", new BigDecimal("20"), true);
        barbeiro.setUsuarioId(usuarioId);
        return barbeiro;
    }

    private Agendamento agendamento(UUID id, UUID barbeiroId) {
        return new Agendamento(
                id,
                UUID.randomUUID(),
                barbeiroId,
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30),
                StatusAgendamento.AGENDADO
        );
    }

    private Pagamento pagamento(UUID agendamentoId) {
        var pagamento = new Pagamento(agendamentoId, new BigDecimal("80"), FormaPagamento.PIX);
        pagamento.setId(UUID.randomUUID());
        pagamento.confirmarPagamento();
        return pagamento;
    }
}
