package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetClientByIdUseCaseTest {

    @Mock
    private ClientStorageRepository repository;

    private GetClientByIdUseCase useCase;

    private ClientDomain clientDomain;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        useCase = new GetClientByIdUseCase(repository);

        clientId = UUID.randomUUID();
        clientDomain = ClientDomain.builder()
                .id(clientId)
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
    void testGetClientByIdSuccess() {
        when(repository.getById(clientId)).thenReturn(clientDomain);

        ClientDomain result = useCase.getById(clientId);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(repository, times(1)).getById(clientId);
    }

    @Test
    void testGetClientByIdDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(),
                "Client not found"
        );

        when(repository.getById(clientId)).thenThrow(dbException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getById(clientId));

        assertEquals(SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to retrieve client by ID: " + clientId, exception.getMessage());
        assertSame(dbException, exception.getCause());
        verify(repository, times(1)).getById(clientId);
    }

    @Test
    void testGetClientByIdGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getById(clientId)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getById(clientId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client retrieval by ID", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(repository, times(1)).getById(clientId);
    }
}
