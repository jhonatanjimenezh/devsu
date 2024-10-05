package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.out.MessageSendRabbit;
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
class CreateClientUseCaseTest {

    @Mock
    private ClientPersonService service;

    @Mock
    private MessageSendRabbit message;

    private CreateClientUseCase useCase;

    private ClientDomain clientDomain;

    @BeforeEach
    void setUp() {
        useCase = new CreateClientUseCase(service, message);

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
    void testCreateClientSuccess() {
        when(service.create(clientDomain)).thenReturn(clientDomain);

        ClientDomain result = useCase.create(clientDomain);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(service, times(1)).create(clientDomain);
    }

    @Test
    void testCreateClientDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(),
                "Person already exists"
        );

        when(service.create(clientDomain)).thenThrow(dbException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.create(clientDomain));

        assertEquals(SPError.CUSTOMER_CONTROLLER_CREATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to create client: " + clientDomain.getClientId(), exception.getMessage());
        assertSame(dbException, exception.getCause());
        verify(service, times(1)).create(clientDomain);
    }

    @Test
    void testCreateClientGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.create(clientDomain)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.create(clientDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client creation", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).create(clientDomain);
    }

}
