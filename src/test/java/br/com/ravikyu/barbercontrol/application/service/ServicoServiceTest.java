package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ServicoService service;

    @Test
    void deveCriarServicoComSucesso() {
        var id = UUID.randomUUID();
        var servico = new Servico(null, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, false);
        var salvo = new Servico(id, "Corte Simples", "Corte básico", new BigDecimal("30.00"), 30, true);

        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.criar(servico);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Corte Simples", response.getNome());
        assertTrue(response.isAtivo());
        verify(repository, times(1)).salvar(argThat(Servico::isAtivo));
    }

    @Test
    void deveDefinirAtivoTrueAoCriarServico() {
        var servico = new Servico(null, "Barba", "Aparar barba", new BigDecimal("20.00"), 20, false);
        var salvo = new Servico(UUID.randomUUID(), "Barba", "Aparar barba", new BigDecimal("20.00"), 20, true);

        when(repository.salvar(any())).thenReturn(salvo);

        service.criar(servico);

        verify(repository).salvar(argThat(Servico::isAtivo));
    }

    @Test
    void deveListarServicosComSucesso() {
        var servicos = List.of(
                new Servico(UUID.randomUUID(), "Corte", "Desc1", new BigDecimal("30.00"), 30, true),
                new Servico(UUID.randomUUID(), "Barba", "Desc2", new BigDecimal("20.00"), 20, true)
        );

        when(repository.listar()).thenReturn(servicos);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Corte", response.get(0).getNome());
        assertEquals("Barba", response.get(1).getNome());
        verify(repository, times(1)).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(repository.listar()).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBuscarServicoPorIdComSucesso() {
        var id = UUID.randomUUID();
        var servico = new Servico(id, "Corte", "Desc", new BigDecimal("30.00"), 30, true);

        when(repository.buscarPorId(id)).thenReturn(Optional.of(servico));

        var response = service.buscar(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Corte", response.getNome());
        verify(repository, times(1)).buscarPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.buscar(id));

        assertEquals("Serviço não encontrado", ex.getMessage());
    }

    @Test
    void deveDeletarServicoComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(repository).deletar(id);

        service.deletar(id);

        verify(repository, times(1)).deletar(id);
    }
}
