package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.usuario.dto.LoginResponse;
import br.com.ravikyu.barbercontrol.application.usuario.dto.UsuarioResponse;
import br.com.ravikyu.barbercontrol.application.usuario.service.UsuarioService;
import br.com.ravikyu.barbercontrol.domain.model.enuns.Role;
import br.com.ravikyu.barbercontrol.infrastructure.security.CustomUserDetailsService;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtAuthenticationFilter;
import br.com.ravikyu.barbercontrol.infrastructure.security.JwtTokenProvider;
import br.com.ravikyu.barbercontrol.infrastructure.security.SecurityConfig;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("deveCadastrarUsuarioAdminComSucesso")
    @WithAnonymousUser
    void deveCadastrarUsuarioAdminComSucesso() throws Exception {
        var response = new UsuarioResponse(UUID.randomUUID(), "admin@barbearia.com", Role.ADMIN, null, null);

        when(service.cadastrar(any())).thenReturn(response);

        mockMvc.perform(post("/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "admin@barbearia.com",
                                    "senha": "senha123",
                                    "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@barbearia.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(service, times(1)).cadastrar(any());
    }

    @Test
    @DisplayName("deveRetornar422AoCadastrarEmailDuplicado")
    @WithAnonymousUser
    void deveRetornar422AoCadastrarEmailDuplicado() throws Exception {
        when(service.cadastrar(any())).thenThrow(new BusinessException("Email já cadastrado"));

        mockMvc.perform(post("/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "duplicado@barbearia.com",
                                    "senha": "senha123",
                                    "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Email já cadastrado"));
    }

    @Test
    @DisplayName("deveRetornar400AoCadastrarEmailInvalido")
    @WithAnonymousUser
    void deveRetornar400AoCadastrarEmailInvalido() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "emailinvalido",
                                    "senha": "senha123",
                                    "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deveFazerLoginComSucesso")
    @WithAnonymousUser
    void deveFazerLoginComSucesso() throws Exception {
        var loginResponse = new LoginResponse("jwt-token-abc", "Bearer");

        when(service.login(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "admin@barbearia.com",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-abc"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));

        verify(service, times(1)).login(any());
    }

    @Test
    @DisplayName("deveRetornar404AoFazerLoginComUsuarioInexistente")
    @WithAnonymousUser
    void deveRetornar404AoFazerLoginComUsuarioInexistente() throws Exception {
        when(service.login(any())).thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "inexistente@barbearia.com",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("deveRenovarTokenComSucesso")
    @WithMockUser(roles = "ADMIN")
    void deveRenovarTokenComSucesso() throws Exception {
        when(service.refreshToken()).thenReturn(new LoginResponse("novo-token", "Bearer"));

        mockMvc.perform(post("/usuarios/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo-token"));

        verify(service).refreshToken();
    }

    @Test
    @DisplayName("deveAlterarSenhaComSucesso")
    @WithMockUser(roles = "ADMIN")
    void deveAlterarSenhaComSucesso() throws Exception {
        mockMvc.perform(patch("/usuarios/alterar-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "senhaAtual": "senha123",
                                    "novaSenha": "novaSenha123"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(service).alterarSenha(any());
    }

    @Test
    @DisplayName("deveRetornarPerfilAutenticadoComSucesso")
    @WithMockUser(roles = "ADMIN")
    void deveRetornarPerfilAutenticadoComSucesso() throws Exception {
        var response = new UsuarioResponse(UUID.randomUUID(), "admin@barbearia.com", Role.ADMIN, null, null);

        when(service.buscarAutenticado()).thenReturn(response);

        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@barbearia.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(service, times(1)).buscarAutenticado();
    }

    @Test
    @DisplayName("deveRetornar403AoBuscarPerfilSemAutenticacao")
    @WithAnonymousUser
    void deveRetornar403AoBuscarPerfilSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deveCadastrarFuncionarioComSucesso")
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarFuncionarioComSucesso() throws Exception {
        var response = new UsuarioResponse(UUID.randomUUID(), "barbeiro@barbearia.com", Role.BARBEIRO, UUID.randomUUID(), null);

        when(service.cadastrarFuncionario(any())).thenReturn(response);

        mockMvc.perform(post("/usuarios/cadastrar-funcionario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "barbeiro@barbearia.com",
                                    "senha": "senha123",
                                    "role": "BARBEIRO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("barbeiro@barbearia.com"))
                .andExpect(jsonPath("$.role").value("BARBEIRO"));

        verify(service, times(1)).cadastrarFuncionario(any());
    }

    @Test
    @DisplayName("deveRetornar403AoCadastrarFuncionarioSemRoleAdmin")
    @WithMockUser(roles = "BARBEIRO")
    void deveRetornar403AoCadastrarFuncionarioSemRoleAdmin() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar-funcionario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "novo@barbearia.com",
                                    "senha": "senha123",
                                    "role": "BARBEIRO"
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
