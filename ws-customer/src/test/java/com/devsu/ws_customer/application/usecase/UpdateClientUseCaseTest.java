package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
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
class UpdateClientUseCaseTest {

    @Mock
    private ClientPersonService service;

    private UpdateClientUseCase useCase;

    private ClientDomain clientDomain;

    @BeforeEach
    void setUp() {
        useCase = new UpdateClientUseCase(service);

        clientDomain = ClientDomain.builder()
                .id(UUID.randomUUID())
                .clientId("client123")
                .password("password")
                .status(true)
                .person(
                        PersonDomain.builder()
                                .id(UUID.randomUUID())
                                .name("John Doe")
                                .gender(new GenderDomain(1, "OTHER"))
                                .age(30)
                                .identification("123456789")
                                .address("123 Main St")
                                .phone("555-1234")
                                .build()
                )
                .build();
    }

    @Test
    void testUpdateClientSuccess() {
        when(service.update(clientDomain)).thenReturn(clientDomain);

        ClientDomain result = useCase.update(clientDomain);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(service, times(1)).update(clientDomain);  // Verifica que el servicio fue llamado una vez
    }

    @Test
    void testUpdateClientDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(),
                "Client not found"
        );

        when(service.update(clientDomain)).thenThrow(dbException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.update(clientDomain));

        assertEquals(SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to update client: " + clientDomain.getClientId(), exception.getMessage());
        assertSame(dbException, exception.getCause());
        verify(service, times(1)).update(clientDomain);  // Verifica que el servicio fue llamado una vez
    }

    @Test
    void testUpdateClientGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.update(clientDomain)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.update(clientDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client update", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).update(clientDomain);  // Verifica que el servicio fue llamado una vez
    }
}
