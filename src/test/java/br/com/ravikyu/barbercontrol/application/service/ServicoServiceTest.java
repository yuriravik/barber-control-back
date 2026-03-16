package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.servico.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.application.servico.service.ServicoService;
import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @Mock
    private UsuarioAutenticadoProvider usuarioProvider;

    @InjectMocks
    private ServicoService service;

    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        when(usuarioProvider.getUsuarioIdAutenticado()).thenReturn(usuarioId);
    }

    @Test
    @DisplayName("deveCriarServicoComSucesso")
    void deveCriarServicoComSucesso() {
        var request = Instancio.of(CriarServicoRequest.class)
                .generate(field(CriarServicoRequest.class, "nome"), gen -> gen.string().minLength(1))
                .create();
        var salvo = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), true)
                .create();

        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.criar(request);

        assertNotNull(response);
        assertTrue(response.ativo());
        verify(repository, times(1)).salvar(argThat(Servico::isAtivo));
    }

    @Test
    @DisplayName("deveDefinirAtivoTrueAoCriarServico")
    void deveDefinirAtivoTrueAoCriarServico() {
        var request = Instancio.of(CriarServicoRequest.class)
                .generate(field(CriarServicoRequest.class, "nome"), gen -> gen.string().minLength(1))
                .create();
        var salvo = Instancio.of(Servico.class).set(field(Servico.class, "ativo"), true).create();

        when(repository.salvar(any())).thenReturn(salvo);

        service.criar(request);

        verify(repository).salvar(argThat(Servico::isAtivo));
    }

    @Test
    @DisplayName("deveListarServicosComSucesso")
    void deveListarServicosComSucesso() {
        var servicos = Instancio.ofList(Servico.class).size(2).create();

        when(repository.listarPorUsuario(usuarioId)).thenReturn(servicos);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(repository, times(1)).listarPorUsuario(usuarioId);
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaServicos")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(repository.listarPorUsuario(usuarioId)).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarServicoPorIdComSucesso")
    void deveBuscarServicoPorIdComSucesso() {
        var servico = Instancio.create(Servico.class);

        when(repository.buscarPorIdEUsuario(servico.getId(), usuarioId)).thenReturn(Optional.of(servico));

        var response = service.buscar(servico.getId());

        assertNotNull(response);
        assertEquals(servico.getId(), response.id());
        verify(repository, times(1)).buscarPorIdEUsuario(servico.getId(), usuarioId);
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoServicoNaoEncontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorIdEUsuario(id, usuarioId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Serviço não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveDeletarServicoComSucesso")
    void deveDeletarServicoComSucesso() {
        var servico = Instancio.create(Servico.class);

        when(repository.buscarPorIdEUsuario(servico.getId(), usuarioId)).thenReturn(Optional.of(servico));
        doNothing().when(repository).deletar(servico.getId());

        service.deletar(servico.getId());

        verify(repository, times(1)).deletar(servico.getId());
    }
}
