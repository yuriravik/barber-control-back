package br.com.ravikyu.barbercontrol.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
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
    void deveRetornar404ParaResourceNotFoundException() {
        var ex = new ResourceNotFoundException("Cliente não encontrado");

        var response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Cliente não encontrado", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void deveRetornar422ParaBusinessException() {
        var ex = new BusinessException("Regra de negócio violada");

        var response = handler.handleBusiness(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().status());
        assertEquals("Business Error", response.getBody().error());
        assertEquals("Regra de negócio violada", response.getBody().message());
    }

    @Test
    void deveRetornar409ParaAgendamentoException() {
        var ex = new AgendamentoException("Conflito de horário");

        var response = handler.handleAgendamento(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Scheduling Conflict", response.getBody().error());
        assertEquals("Conflito de horário", response.getBody().message());
    }

    @Test
    void deveRetornar400ParaIllegalArgumentException() {
        var ex = new IllegalArgumentException("Argumento inválido");

        var response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Argumento inválido", response.getBody().message());
    }

    @Test
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
    void deveRetornar500ParaExcecaoGenerica() {
        var ex = new RuntimeException("Erro inesperado");

        var response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().message());
    }

    @Test
    void deveVerificarMensagemDeResourceNotFoundException() {
        var ex = new ResourceNotFoundException("Barbeiro não encontrado");

        assertEquals("Barbeiro não encontrado", ex.getMessage());
    }

    @Test
    void deveVerificarMensagemDeBusinessException() {
        var ex = new BusinessException("Comissão inválida");

        assertEquals("Comissão inválida", ex.getMessage());
    }

    @Test
    void deveVerificarMensagemDeAgendamentoException() {
        var ex = new AgendamentoException("Horário indisponível");

        assertEquals("Horário indisponível", ex.getMessage());
    }

    @Test
    void deveVerificarCamposDoErrorResponse() {
        var response = new ErrorResponse(404, "Not Found", "Recurso não encontrado");

        assertEquals(404, response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Recurso não encontrado", response.message());
        assertNotNull(response.timestamp());
    }
}
