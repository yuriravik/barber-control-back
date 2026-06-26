package br.com.ravikyu.barbercontrol.infrastructure.persistence.repository;

import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.infrastructure.persistence.entity.UsuarioEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryImplTest {

    @Mock
    private UsuarioJpaRepository jpaRepository;

    @InjectMocks
    private UsuarioRepositoryImpl repository;

    private UsuarioEntity entidadeValida(String role) {
        return Instancio.of(UsuarioEntity.class)
                .set(field(UsuarioEntity.class, "role"), role)
                .generate(field(UsuarioEntity.class, "email"), gen -> gen.net().email())
                .create();
    }

    @Test
    @DisplayName("deveSalvarUsuarioComSucesso")
    void deveSalvarUsuarioComSucesso() {
        var entity = entidadeValida("ADMIN");
        var usuario = new br.com.ravikyu.barbercontrol.domain.model.Usuario(
                entity.getEmail(), "senha_hash", Role.ADMIN);
        usuario.setId(entity.getId());
        usuario.setAdminId(entity.getAdminId());
        usuario.setBarbeiroId(entity.getBarbeiroId());

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(usuario);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getEmail(), result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deveBuscarUsuarioPorEmailComSucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        var entity = entidadeValida("BARBEIRO");

        when(jpaRepository.findByEmail(entity.getEmail())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorEmail(entity.getEmail());

        assertTrue(result.isPresent());
        assertEquals(entity.getEmail(), result.get().getEmail());
        assertEquals(Role.BARBEIRO, result.get().getRole());
        verify(jpaRepository, times(1)).findByEmail(entity.getEmail());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoEmailNaoEncontrado")
    void deveRetornarVazioQuandoEmailNaoEncontrado() {
        var email = "naoexiste@email.com";

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        var result = repository.buscarPorEmail(email);

        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("deveBuscarUsuarioPorIdComSucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        var entity = entidadeValida("SECRETARIA");

        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = repository.buscarPorId(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(Role.SECRETARIA, result.get().getRole());
        verify(jpaRepository, times(1)).findById(entity.getId());
    }

    @Test
    @DisplayName("deveRetornarVazioQuandoIdNaoEncontrado")
    void deveRetornarVazioQuandoIdNaoEncontrado() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = repository.buscarPorId(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deveRetornarTrueQuandoEmailJaExiste")
    void deveRetornarTrueQuandoEmailJaExiste() {
        var email = "existente@email.com";

        when(jpaRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(repository.existePorEmail(email));
        verify(jpaRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("deveRetornarFalseQuandoEmailNaoExiste")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        var email = "novo@email.com";

        when(jpaRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(repository.existePorEmail(email));
        verify(jpaRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("deveSalvarUsuarioComAdminIdEBarbeiroId")
    void deveSalvarUsuarioComAdminIdEBarbeiroId() {
        var adminId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var entity = entidadeValida("BARBEIRO");
        entity.setAdminId(adminId);
        entity.setBarbeiroId(barbeiroId);

        var usuario = new br.com.ravikyu.barbercontrol.domain.model.Usuario(
                entity.getEmail(), "senha_hash", Role.BARBEIRO);
        usuario.setId(entity.getId());
        usuario.setAdminId(adminId);
        usuario.setBarbeiroId(barbeiroId);

        when(jpaRepository.save(any())).thenReturn(entity);

        var result = repository.salvar(usuario);

        assertNotNull(result);
        assertEquals(adminId, result.getAdminId());
        assertEquals(barbeiroId, result.getBarbeiroId());
    }
}
