package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.usuario.dto.AlterarSenhaRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastrarFuncionarioRequest;
import br.com.ravikyu.barbercontrol.application.usuario.dto.CadastroRequest;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import br.com.ravikyu.barbercontrol.domain.model.Usuario;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.domain.repository.UsuarioRepository;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.UsuarioAutenticadoProvider;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

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
    @DisplayName("deveLancarExcecaoAoCadastrarBarbeiroNaRota publica")
    void deveLancarExcecaoAoCadastrarBarbeiroNaRotaPublica() {
        var adminId = UUID.randomUUID();
        var dto = new CadastroRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", adminId);

        when(repository.existePorEmail(dto.email())).thenReturn(false);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertTrue(ex.getMessage().contains("cadastrar-funcionario"));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("deveLancarExcecaoAoCadastrarSecretariaNaRotaPublica")
    void deveLancarExcecaoAoCadastrarSecretariaNaRotaPublica() {
        var dto = new CadastroRequest("sec@barbearia.com", "senha123", "SECRETARIA", null);

        when(repository.existePorEmail(dto.email())).thenReturn(false);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrar(dto));

        assertTrue(ex.getMessage().contains("cadastrar-funcionario"));
        verify(repository, never()).salvar(any());
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

    @Test
    @DisplayName("deveRenovarTokenComSucesso")
    void deveRenovarTokenComSucesso() {
        var usuario = adminValido();
        usuario.setEmail("admin@barbearia.com");

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(tokenProvider.gerarToken(usuario.getEmail())).thenReturn("token-novo");

        var response = service.refreshToken();

        assertEquals("token-novo", response.token());
    }

    @Test
    @DisplayName("deveAlterarSenhaComSucesso")
    void deveAlterarSenhaComSucesso() {
        var usuario = adminValido();
        usuario.setSenha("hash-antigo");

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(passwordEncoder.matches("senha123", "hash-antigo")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("hash-novo");

        service.alterarSenha(new AlterarSenhaRequest("senha123", "novaSenha123"));

        verify(repository).salvar(argThat(u -> "hash-novo".equals(u.getSenha())));
    }

    @Test
    @DisplayName("deveCadastrarFuncionarioBarbeiroComSucesso")
    void deveCadastrarFuncionarioBarbeiroComSucesso() {
        var adminId = UUID.randomUUID();
        var barbeiroId = UUID.randomUUID();
        var dto = new CadastrarFuncionarioRequest("barbeiro@barbearia.com", "senha123", "BARBEIRO", barbeiroId);

        var savedBarbeiro = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.BARBEIRO)
                .set(field(Usuario.class, "adminId"), adminId)
                .set(field(Usuario.class, "barbeiroId"), barbeiroId)
                .create();

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(usuarioAutenticadoProvider.getUsuarioIdAutenticado()).thenReturn(adminId);
        when(repository.buscarPorId(adminId)).thenReturn(java.util.Optional.of(adminValido()));
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash");
        when(repository.salvar(any())).thenReturn(savedBarbeiro);

        var response = service.cadastrarFuncionario(dto);

        assertNotNull(response);
        assertEquals(Role.BARBEIRO, response.role());
        assertEquals(adminId, response.adminId());
        verify(repository).salvar(argThat(u ->
                adminId.equals(u.getAdminId()) && barbeiroId.equals(u.getBarbeiroId())));
    }

    @Test
    @DisplayName("deveCadastrarFuncionarioSecretariaComSucesso")
    void deveCadastrarFuncionarioSecretariaComSucesso() {
        var adminId = UUID.randomUUID();
        var dto = new CadastrarFuncionarioRequest("sec@barbearia.com", "senha123", "SECRETARIA", null);

        var savedSec = Instancio.of(Usuario.class)
                .generate(field(Usuario.class, "email"), gen -> gen.net().email())
                .set(field(Usuario.class, "role"), Role.SECRETARIA)
                .set(field(Usuario.class, "adminId"), adminId)
                .set(field(Usuario.class, "barbeiroId"), null)
                .create();

        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(usuarioAutenticadoProvider.getUsuarioIdAutenticado()).thenReturn(adminId);
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash");
        when(repository.salvar(any())).thenReturn(savedSec);

        var response = service.cadastrarFuncionario(dto);

        assertNotNull(response);
        assertEquals(Role.SECRETARIA, response.role());
        assertEquals(adminId, response.adminId());
        assertNull(response.barbeiroId());
        verify(repository).salvar(argThat(u -> adminId.equals(u.getAdminId())));
    }

    @Test
    @DisplayName("deveLancarExcecaoAoCadastrarFuncionarioComEmailJaExistente")
    void deveLancarExcecaoAoCadastrarFuncionarioComEmailJaExistente() {
        var dto = new CadastrarFuncionarioRequest("dup@barbearia.com", "senha123", "BARBEIRO", null);

        when(repository.existePorEmail(dto.email())).thenReturn(true);

        var ex = assertThrows(BusinessException.class, () -> service.cadastrarFuncionario(dto));

        assertEquals("Email já cadastrado", ex.getMessage());
        verify(repository, never()).salvar(any());
    }
}
