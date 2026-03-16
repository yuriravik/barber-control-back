package br.com.ravikyu.barbercontrol.infrastructure.web.exception;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("deveRetornar404ParaResourceNotFoundException")
    void deveRetornar404ParaResourceNotFoundException() {
        var mensagem = Instancio.create(String.class);
        var ex = new ResourceNotFoundException(mensagem);

        var response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals(mensagem, response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("deveRetornar422ParaBusinessException")
    void deveRetornar422ParaBusinessException() {
        var mensagem = Instancio.create(String.class);
        var ex = new BusinessException(mensagem);

        var response = handler.handleBusiness(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().status());
        assertEquals("Business Error", response.getBody().error());
        assertEquals(mensagem, response.getBody().message());
    }

    @Test
    @DisplayName("deveRetornar409ParaAgendamentoException")
    void deveRetornar409ParaAgendamentoException() {
        var mensagem = Instancio.create(String.class);
        var ex = new AgendamentoException(mensagem);

        var response = handler.handleAgendamento(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Scheduling Conflict", response.getBody().error());
        assertEquals(mensagem, response.getBody().message());
    }

    @Test
    @DisplayName("deveRetornar400ParaIllegalArgumentException")
    void deveRetornar400ParaIllegalArgumentException() {
        var mensagem = Instancio.create(String.class);
        var ex = new IllegalArgumentException(mensagem);

        var response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(mensagem, response.getBody().message());
    }

    @Test
    @DisplayName("deveRetornar400ParaMethodArgumentNotValidException")
    void deveRetornar400ParaMethodArgumentNotValidException() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("dto", "nome", "Nome é obrigatório");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Validation Error", response.getBody().error());
        assertTrue(response.getBody().message().contains("nome: Nome é obrigatório"));
    }

    @Test
    @DisplayName("deveRetornar400ParaValidacaoComMultiplosCampos")
    void deveRetornar400ParaValidacaoComMultiplosCampos() {
        var bindingResult = mock(BindingResult.class);
        var errors = List.of(
                new FieldError("dto", "nome", "Nome é obrigatório"),
                new FieldError("dto", "email", "Email é obrigatório")
        );
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidation(ex);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().message().contains("nome: Nome é obrigatório"));
        assertTrue(response.getBody().message().contains("email: Email é obrigatório"));
    }

    @Test
    @DisplayName("deveRetornar500ParaExcecaoGenerica")
    void deveRetornar500ParaExcecaoGenerica() {
        var ex = new RuntimeException(Instancio.create(String.class));

        var response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().message());
    }

    @Test
    @DisplayName("deveVerificarMensagemDeResourceNotFoundException")
    void deveVerificarMensagemDeResourceNotFoundException() {
        var mensagem = Instancio.create(String.class);
        var ex = new ResourceNotFoundException(mensagem);

        assertEquals(mensagem, ex.getMessage());
    }

    @Test
    @DisplayName("deveVerificarMensagemDeBusinessException")
    void deveVerificarMensagemDeBusinessException() {
        var mensagem = Instancio.create(String.class);
        var ex = new BusinessException(mensagem);

        assertEquals(mensagem, ex.getMessage());
    }

    @Test
    @DisplayName("deveVerificarMensagemDeAgendamentoException")
    void deveVerificarMensagemDeAgendamentoException() {
        var mensagem = Instancio.create(String.class);
        var ex = new AgendamentoException(mensagem);

        assertEquals(mensagem, ex.getMessage());
    }

    @Test
    @DisplayName("deveVerificarCamposDoErrorResponse")
    void deveVerificarCamposDoErrorResponse() {
        var mensagem = Instancio.create(String.class);
        var error = Instancio.create(String.class);
        var response = new ErrorResponse(404, error, mensagem);

        assertEquals(404, response.status());
        assertEquals(error, response.error());
        assertEquals(mensagem, response.message());
        assertNotNull(response.timestamp());
    }
}
