package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.cliente.dto.CriarClienteRequest;
import br.com.ravikyu.barbercontrol.application.cliente.service.ClienteService;
import br.com.ravikyu.barbercontrol.domain.model.Cliente;
import br.com.ravikyu.barbercontrol.domain.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveCriarClienteComSucesso() {

        // Arrange
        CriarClienteRequest dto = new CriarClienteRequest(
                "Yuri",
                "85999999999",
                "yuri@email.com"
        );

        Cliente clienteSalvo = new Cliente(
                UUID.randomUUID(),
                dto.nome(),
                dto.telefone(),
                dto.email()
        );

        when(repository.salvar(any())).thenReturn(clienteSalvo);

        // Act
        var response = service.criar(dto);

        // Assert
        assertNotNull(response);
        assertEquals("Yuri", response.nome());
        verify(repository, times(1)).salvar(any());
    }
}