package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllClientsUseCaseTest {

    @Mock
    private ClientStorageRepository repository;

    private GetAllClientsUseCase useCase;

    private List<ClientDomain> clientList;

    @BeforeEach
    void setUp() {
        useCase = new GetAllClientsUseCase(repository);

        clientList = new ArrayList<>();
        clientList.add(ClientDomain.builder()
                .clientId("client123")
                .password("password")
                .status(true)
                .build());
    }

    @Test
    void testGetAllClientsSuccess() {
        when(repository.getAll()).thenReturn(clientList);

        List<ClientDomain> result = useCase.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("client123", result.get(0).getClientId());

        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllClientsDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(),
                "Database error"
        );

        when(repository.getAll()).thenThrow(dbException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAll());

        assertEquals(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to retrieve clients", exception.getMessage());
        assertSame(dbException, exception.getCause());

        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllClientsGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getAll()).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAll());

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());

        verify(repository, times(1)).getAll();
    }
}
