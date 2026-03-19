package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastroRequest;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UsuarioService service;

    private Usuario adminValido() {
        return Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.ADMIN)
                .set(field(Usuario.class, "adminId"), null)
                .create();
    }

    @Test
    @DisplayName("deveCadastrarUsuarioAdminComSucesso")
    void deveCadastrarUsuarioAdminComSucesso() {
        var dto = new CadastroRequest("admin@barbearia.com", "senha123", "ADMIN", null);
        var salvo = adminValido();
        salvo.setEmail("admin@barbearia.com");

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash");
        when(repository.salvar(any())).thenReturn(salvo);

        var response = service.cadastrar(dto);

        assertNotNull(response);
        assertEquals("admin@barbearia.com", response.email());
        assertNull(response.adminId());
        verify(repository).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoAoCadastrarEmailJaExistente")
    void deveLancarExcecaoAoCadastrarEmailJaExistente() {
        var dto = new CadastroRequest("duplicado@barbearia.com", "senha123", "ADMIN", null);

        when(repository.existePorEmail(dto.email())).thenReturn(true);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertEquals("Email já cadastrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveCadastrarUsuarioBarbeiroVinculadoAoAdmin")
    void deveCadastrarUsuarioBarbeiroVinculadoAoAdmin() {
        var adminId = UUID.randomUUID();
        var dto = new CadastroRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", adminId);
        var admin = adminValido();
        admin.setId(adminId);

        var savedBarbeiro = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.BARBEIRO)
                .set(field(Usuario.class, "adminId"), adminId)
                .create();

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(repository.buscarPorId(adminId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash");
        when(repository.salvar(any())).thenReturn(savedBarbeiro);

        var response = service.cadastrar(dto);

        assertNotNull(response);
        assertEquals(Role.BARBEIRO, response.role());
        assertEquals(adminId, response.adminId());
        verify(repository).salvar(argThat(u -> adminId.equals(u.getAdminId())));
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoBarbeiroSemAdminId")
    void deveLancarExcecaoQuandoBarbeiroSemAdminId() {
        var dto = new CadastroRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", null);

        when(repository.existePorEmail(dto.email())).thenReturn(false);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertEquals("adminId é obrigatório para usuários com role BARBEIRO", ex.getMessage());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoAdminIdNaoEncontrado")
    void deveLancarExcecaoQuandoAdminIdNaoEncontrado() {
        var adminId = UUID.randomUUID();
        var dto = new CadastroRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", adminId);

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(repository.buscarPorId(adminId)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () -> service.cadastrar(dto));

        assertEquals("Administrador não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoAdminIdNaoPertenceAAdmin")
    void deveLancarExcecaoQuandoAdminIdNaoPertenceAAdmin() {
        var adminId = UUID.randomUUID();
        var dto = new CadastroRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", adminId);

        var outroBarbeiro = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.BARBEIRO)
                .create();
        outroBarbeiro.setId(adminId);

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(repository.buscarPorId(adminId)).thenReturn(Optional.of(outroBarbeiro));

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertEquals("O usuário vinculado deve ter role ADMIN", ex.getMessage());
    }

    @Test
    @DisplayName("deveLancarExcecaoQuandoAdminTentaVincularSeAOutroAdmin")
    void deveLancarExcecaoQuandoAdminTentaVincularSeAOutroAdmin() {
        var adminId = UUID.randomUUID();
        var dto = new CadastroRequest("admin2@barbearia.com", "senha123", "ADMIN", adminId);

        when(repository.existePorEmail(dto.email())).thenReturn(false);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertEquals("Usuários com role ADMIN não podem ser vinculados a outro administrador", ex.getMessage());
        verify(repository, never()).salvar(any());
    }
}
