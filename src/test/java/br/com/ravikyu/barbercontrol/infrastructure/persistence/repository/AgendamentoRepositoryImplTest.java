package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.Agendamento;
import br.com.ravikyu.barbercontrol.domain.model.enuns.StatusAgendamento;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.AgendamentoEntity;
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
class AgendamentoRepositoryImplTest {

    @Mock
    private AgendamentoJpaRepository jpaRepository;

    @InjectMocks
    private AgendamentoRepositoryImpl repository;

    private AgendamentoEntity entidadeValida(String status) {
        return Instancio.of(AgendamentoEntity.class)
                .set(field(AgendamentoEntity.class, "status"), status)
                .create();
    }

    @Test
    @DisplayName("deveSalvarAgendamentoComSucesso")
    void deveSalvarAgendamentoComSucesso() {
        var entity = entidadeValida("AGENDADO");
        var agendamento = Instancio.of(Agendamento.class)
                .set(field(Agendamento.class, "status"), StatusAgendamento.AGENDADO)
                .create();

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(agendamento);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(StatusAgendamento.valueOf(entity.getStatus()), result.getStatus());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveBuscarAgendamentoPorIdComSucesso")
    void deveBuscarAgendamentoPorIdComSucesso() {
        var entity = entidadeValida("AGENDADO");

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(StatusAgendamento.AGENDADO, result.get().getStatus());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoAgendamentoNaoEncontrado")
    void deveRetornarVazioQuandoAgendamentoNaoEncontrado() {
        var id = Instancio.create(java.util.UUID.class);

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveListarAgendamentosComSucesso")
    void deveListarAgendamentosComSucesso() {
        var entities = List.of(
                entidadeValida("AGENDADO"),
                entidadeValida("CANCELADO")
        );

        when(jpaRepository.findAll()).thenReturn(entities);

        var result = repository.listar();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(StatusAgendamento.AGENDADO, result.get(0).getStatus());
        assertEquals(StatusAgendamento.CANCELADO, result.get(1).getStatus());
        verify(jpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveRetornarListaVaziaQuandoNaoHaAgendamentos")
    void deveRetornarListaVaziaQuandoNaoHaAgendamentos() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.listar();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveDeletarAgendamentoComSucesso")
    void deveDeletarAgendamentoComSucesso() {
        var id = Instancio.create(java.util.UUID.class);

        doNothing().when(jpaRepository).deleteById(id);

        repository.deletar(id);

        verify(jpaRepository, times(1)).deleteById(id);
    }
}
