package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
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
class BarbeiroServiceTest {

    @Mock
    private BarbeiroRepository repository;

    @InjectMocks
    private BarbeiroService service;

    @Test
    void deveCriarBarbeiroComSucesso() {
        var dto = new CriarBarbeiroRequest("Carlos", "Corte", new BigDecimal("20.00"), true);
        var id = UUID.randomUUID();
        var salvo = new Barbeiro(id, "Carlos", "Corte", new BigDecimal("20.00"), true);

        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.criar(dto);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Carlos", response.nome());
        assertEquals("Corte", response.especialidade());
        assertEquals(new BigDecimal("20.00"), response.percentualComissao());
        assertTrue(response.ativo());
        verify(repository, times(1)).salvar(any());
    }

    @Test
    void deveListarBarbeirosComSucesso() {
        var barbeiros = List.of(
                new Barbeiro(UUID.randomUUID(), "Carlos", "Corte", new BigDecimal("20.00"), true),
                new Barbeiro(UUID.randomUUID(), "Ana", "Barba", new BigDecimal("15.00"), false)
        );

        when(repository.listar()).thenReturn(barbeiros);

        var response = service.listar();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Carlos", response.get(0).nome());
        assertEquals("Ana", response.get(1).nome());
        verify(repository, times(1)).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaBarbeiros() {
        when(repository.listar()).thenReturn(List.of());

        var response = service.listar();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveDesativarBarbeiroComSucesso() {
        var id = UUID.randomUUID();
        var barbeiro = new Barbeiro(id, "Carlos", "Corte", new BigDecimal("20.00"), true);
        var barbeiroInativo = new Barbeiro(id, "Carlos", "Corte", new BigDecimal("20.00"), false);

        when(repository.buscarPorId(id)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenReturn(barbeiroInativo);

        service.desativar(id);

        verify(repository, times(1)).buscarPorId(id);
        verify(repository, times(1)).salvar(argThat(b -> !b.isAtivo()));
    }

    @Test
    void deveLancarExcecaoAoDesativarBarbeiroNaoEncontrado() {
        var id = UUID.randomUUID();

        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.desativar(id));

        assertEquals("Barbeiro não encontrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    void deveManterDadosBarbeiroAoDesativar() {
        var id = UUID.randomUUID();
        var barbeiro = new Barbeiro(id, "José", "Sobrancelha", new BigDecimal("25.00"), true);

        when(repository.buscarPorId(id)).thenReturn(Optional.of(barbeiro));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.desativar(id);

        verify(repository).salvar(argThat(b ->
                b.getId().equals(id) &&
                        "José".equals(b.getNome()) &&
                        "Sobrancelha".equals(b.getEspecialidade()) &&
                        new BigDecimal("25.00").equals(b.getPercentualComissao()) &&
                        !b.isAtivo()
        ));
    }
}
