package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ServicoService service;

    @Test
    @DisplayName("deveCriarServicoComSucesso")
    void deveCriarServicoComSucesso() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "id"), null)
                .set(field(Servico.class, "ativo"), false)
                .create();
        var salvo = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), true)
                .create();

        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.criar(servico);

        assertNotNull(response);
        assertTrue(response.isAtivo());
        verify(repository, times(1)).salvar(argThat(Servico::isAtivo));
    }

    @Test
    @DisplayName("deveDefinirAtivoTrueAoCriarServico")
    void deveDefinirAtivoTrueAoCriarServico() {
        var servico = Instancio.of(Servico.class)
                .set(field(Servico.class, "ativo"), false)
                .create();
        var salvo = Instancio.of(Servico.class).set(field(Servico.class, "ativo"), true).create();

        when(repository.salvar(any())).thenReturn(salvo);

        service.criar(servico);

        verify(repository).salvar(argThat(Servico::isAtivo));
    }

    @Test
    @DisplayName("deveListarServicosComSucesso")
    void deveListarServicosComSucesso() {
        var servicos = Instancio.ofList(Servico.class).size(2).create();

        when(repository.listar()).thenReturn(servicos);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(repository, times(1)).listar();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaServicos")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(repository.listar()).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("deveBuscarServicoPorIdComSucesso")
    void deveBuscarServicoPorIdComSucesso() {
        var servico = Instancio.create(Servico.class);

        when(repository.buscarPorId(servico.getId())).thenReturn(Optional.of(servico));

        var response = service.buscar(servico.getId());

        assertNotNull(response);
        assertEquals(servico.getId(), response.getId());
        verify(repository, times(1)).buscarPorId(servico.getId());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoServicoNaoEncontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        var id = Instancio.create(java.util.UUID.class);

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Serviço não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveDeletarServicoComSucesso")
    void deveDeletarServicoComSucesso() {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(repository).deletar(id);

        service.deletar(id);

        verify(repository, times(1)).deletar(id);
    }
}
