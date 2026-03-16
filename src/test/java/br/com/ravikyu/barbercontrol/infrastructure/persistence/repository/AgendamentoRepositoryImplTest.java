package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.AgendamentoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoRepositoryImplTest {

    @Mock
    private AgendamentoJpaRepository jpaRepository;

    @InjectMocks
    private AgendamentoRepositoryImpl repository;

    private final LocalDateTime DATA_HORA = LocalDateTime.now().plusDays(1);

    @Test
    void deveSalvarAgendamentoComSucesso() {
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();

        var agendamento = new Agendamento(null, clienteId, barbeiroId, servicoId,
                DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO");

        var entity = new AgendamentoEntity(id, clienteId, barbeiroId, servicoId,
                DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO");

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(agendamento);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(clienteId, result.getClienteId());
        assertEquals(barbeiroId, result.getBarbeiroId());
        assertEquals(servicoId, result.getServicoId());
        assertEquals(DATA_HORA, result.getDataHoraInicio());
        assertEquals(DATA_HORA.plusMinutes(30), result.getDataHoraFim());
        assertEquals("AGENDADO", result.getStatus());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    void deveBuscarAgendamentoPorIdComSucesso() {
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var servicoId = UUID.randomUUID();

        var entity = new AgendamentoEntity(id, clienteId, barbeiroId, servicoId,
                DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("AGENDADO", result.get().getStatus());
        verify(jpaRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoAgendamentoNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveListarAgendamentosComSucesso() {
        var entities = List.of(
                new AgendamentoEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        DATA_HORA, DATA_HORA.plusMinutes(30), "AGENDADO"),
                new AgendamentoEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        DATA_HORA.plusDays(1), DATA_HORA.plusDays(1).plusMinutes(45), "CANCELADO")
        );

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AGENDADO", result.get(0).getStatus());
        assertEquals("CANCELADO", result.get(1).getStatus());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaAgendamentos() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    void deveDeletarAgendamentoComSucesso() {
        var id = UUID.randomUUID();

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
