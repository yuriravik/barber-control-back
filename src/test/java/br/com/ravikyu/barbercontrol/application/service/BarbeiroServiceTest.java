package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.AtualizarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.dto.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.barbeiro.service.BarbeiroService;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarbeiroServiceTest {

    @Mock
    private BarbeiroRepository repository;

    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private BarbeiroService service;

    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        when(usuarioProvider.getUsuarioIdAutenticado()).thenReturn(usuarioId);
    }

    private Barbeiro barbeiroValido() {
        return Instancio.of(Barbeiro.class)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();
    }

    @Test
    @DisplayName("deveCriarBarbeiroComSucesso")
    void deveCriarBarbeiroComSucesso() {
        var salvo = barbeiroValido();
        var dto = new CriarBarbeiroRequest(
                salvo.getNome(), salvo.getEspecialidade(), salvo.getPercentualComissao());

        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.criar(dto);

        assertNotNull(response);
        assertEquals(salvo.getId(), response.id());
        assertEquals(salvo.getNome(), response.nome());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("deveListarBarbeirosComSucesso")
    void deveListarBarbeirosComSucesso() {
        var barbeiros = Instancio.ofList(Barbeiro.class)
                .size(2)
                .generate(field(Barbeiro.class, "percentualComissao"),
                        gen -> gen.math().bigDecimal().scale(2).range(BigDecimal.ONE, new BigDecimal("99")))
                .create();

        when(repository.listarPorUsuario(usuarioId)).thenReturn(barbeiros);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(repository, times(1)).listarPorUsuario(usuarioId);
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaBarbeiros")
    void deveRetornarListaVaziaQuandoNaoHaBarbeiros() {
        when(repository.listarPorUsuario(usuarioId)).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarBarbeiroComSucesso")
    void deveBuscarBarbeiroComSucesso() {
        var barbeiro = barbeiroValido();
        when(repository.buscarPorIdEUsuario(barbeiro.getId(), usuarioId)).thenReturn(Optional.of(barbeiro));

        var response = service.buscar(barbeiro.getId());

        assertEquals(barbeiro.getId(), response.id());
        verify(repository).buscarPorIdEUsuario(barbeiro.getId(), usuarioId);
    }

    @Test
    @DisplayName("deveAtualizarBarbeiroComSucesso")
    void deveAtualizarBarbeiroComSucesso() {
        var barbeiro = barbeiroValido();
        var dto = new AtualizarBarbeiroRequest("Novo Nome", "Nova Especialidade", new BigDecimal("25.00"));

        when(repository.buscarPorIdEUsuario(barbeiro.getId(), usuarioId)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.atualizar(barbeiro.getId(), dto);

        assertNotNull(response);
        assertEquals("Novo Nome", response.nome());
        assertEquals("Nova Especialidade", response.especialidade());
        assertEquals(new BigDecimal("25.00"), response.percentualComissao());
        verify(repository).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoAoAtualizarBarbeiroNaoEncontrado")
    void deveLancarExcecaoAoAtualizarBarbeiroNaoEncontrado() {
        var id = UUID.randomUUID();
        var dto = new AtualizarBarbeiroRequest("Nome", "Esp", BigDecimal.TEN);

        when(repository.buscarPorIdEUsuario(id, usuarioId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.atualizar(id, dto));

        assertEquals("Barbeiro não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveManterAtivoAoAtualizar")
    void deveManterAtivoAoAtualizar() {
        var barbeiro = barbeiroValido();
        var dto = new AtualizarBarbeiroRequest("Nome Atualizado", null, new BigDecimal("10.00"));

        when(repository.buscarPorIdEUsuario(barbeiro.getId(), usuarioId)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.atualizar(barbeiro.getId(), dto);

        verify(repository).salvar(argThat(b -> b.isAtivo() == barbeiro.isAtivo()));
    }

    @Test
    @DisplayName("deveDesativarBarbeiroComSucesso")
    void deveDesativarBarbeiroComSucesso() {
        var barbeiro = barbeiroValido();

        when(repository.buscarPorIdEUsuario(barbeiro.getId(), usuarioId)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.desativar(barbeiro.getId());

        verify(repository, times(1)).buscarPorIdEUsuario(barbeiro.getId(), usuarioId);
        verify(repository, times(1)).salvar(argThat(b -> !b.isAtivo()));
    }

    @Test
    @DisplayName("deveLancarExcecaoAoDesativarBarbeiroNaoEncontrado")
    void deveLancarExcecaoAoDesativarBarbeiroNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorIdEUsuario(id, usuarioId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.desativar(id));

        assertEquals("Barbeiro não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveManterDadosBarbeiroAoDesativar")
    void deveManterDadosBarbeiroAoDesativar() {
        var barbeiro = barbeiroValido();

        when(repository.buscarPorIdEUsuario(barbeiro.getId(), usuarioId)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.desativar(barbeiro.getId());

        verify(repository).salvar(argThat(b ->
                b.getId().equals(barbeiro.getId()) &&
                        barbeiro.getNome().equals(b.getNome()) &&
                        barbeiro.getEspecialidade().equals(b.getEspecialidade()) &&
                        barbeiro.getPercentualComissao().equals(b.getPercentualComissao()) &&
                        !b.isAtivo()
        ));
    }
}
