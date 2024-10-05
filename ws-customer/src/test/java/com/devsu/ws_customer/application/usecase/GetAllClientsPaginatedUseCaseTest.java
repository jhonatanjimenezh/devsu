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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllClientsPaginatedUseCaseTest {

    @Mock
    private ClientStorageRepository repository;

    private GetAllClientsPaginatedUseCase useCase;

    private Page<ClientDomain> clientPage;

    @BeforeEach
    void setUp() {
        useCase = new GetAllClientsPaginatedUseCase(repository);

        ClientDomain clientDomain = ClientDomain.builder()
                .clientId("client123")
                .password("password")
                .status(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        clientPage = new PageImpl<>(List.of(clientDomain), pageable, 1);
    }

    @Test
    void testGetAllClientsPaginatedSuccess() {
        when(repository.getAllPaginated(0, 10)).thenReturn(clientPage);

        Page<ClientDomain> result = useCase.getAllPaginated(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("client123", result.getContent().get(0).getClientId());

        verify(repository, times(1)).getAllPaginated(0, 10);
    }

    @Test
    void testGetAllClientsPaginatedDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(),
                "Database error"
        );

        when(repository.getAllPaginated(0, 10)).thenThrow(dbException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAllPaginated(0, 10));

        assertEquals(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to retrieve clients paginated", exception.getMessage());
        assertSame(dbException, exception.getCause());

        verify(repository, times(1)).getAllPaginated(0, 10);
    }

    @Test
    void testGetAllClientsPaginatedGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getAllPaginated(0, 10)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAllPaginated(0, 10));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Unexpected error during client paginated retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());

        verify(repository, times(1)).getAllPaginated(0, 10);
    }
}
