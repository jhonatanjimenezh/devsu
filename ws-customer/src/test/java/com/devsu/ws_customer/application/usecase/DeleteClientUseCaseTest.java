package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.service.ClientPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteClientUseCaseTest {

    @Mock
    private ClientPersonService service;

    private DeleteClientUseCase useCase;

    private UUID clientId;

    @BeforeEach
    void setUp() {
        useCase = new DeleteClientUseCase(service);
        clientId = UUID.randomUUID();
    }

    @Test
    void testDeleteClientSuccess() {
        doNothing().when(service).delete(clientId);

        assertDoesNotThrow(() -> useCase.delete(clientId));

        verify(service, times(1)).delete(clientId);
    }

    @Test
    void testDeleteClientDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(),
                "Client not found"
        );

        doThrow(dbException).when(service).delete(clientId);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.delete(clientId));

        assertEquals(SPError.CUSTOMER_CONTROLLER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to delete client with ID: " + clientId, exception.getMessage());
        assertSame(dbException, exception.getCause());

        verify(service, times(1)).delete(clientId);
    }

    @Test
    void testDeleteClientGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        doThrow(genericException).when(service).delete(clientId);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.delete(clientId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client deletion", exception.getMessage());
        assertSame(genericException, exception.getCause());

        verify(service, times(1)).delete(clientId);
    }
}
