package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.application.port.out.TransactionStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionByIdUseCaseTest {

    @Mock
    private TransactionStorageRepository repository;

    private GetTransactionByIdUseCase useCase;

    private UUID transactionId;
    private TransactionDomain transactionDomain;

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionByIdUseCase(repository);
        transactionId = UUID.randomUUID();
        transactionDomain = TransactionDomain.builder()
                .id(transactionId)
                .build();
    }

    @Test
    void testGetTransactionByIdSuccess() {
        when(repository.getById(any(UUID.class))).thenReturn(transactionDomain);

        TransactionDomain result = useCase.getById(transactionId);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(repository, times(1)).getById(transactionId);
    }

    @Test
    void testGetTransactionByIdDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                "Error retrieving transaction"
        );

        when(repository.getById(any(UUID.class))).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.getById(transactionId));

        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error retrieving transaction", exception.getMessage());
        verify(repository, times(1)).getById(transactionId);
    }

    @Test
    void testGetTransactionByIdCustomException() {
        CustomException customException = new CustomException(
                SPError.GENERIC_ERROR.getErrorCode(),
                "Custom error"
        );

        when(repository.getById(any(UUID.class))).thenThrow(customException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getById(transactionId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Custom error", exception.getMessage());
        verify(repository, times(1)).getById(transactionId);
    }

    @Test
    void testGetTransactionByIdGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getById(any(UUID.class))).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getById(transactionId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during transaction retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(repository, times(1)).getById(transactionId);
    }
}
