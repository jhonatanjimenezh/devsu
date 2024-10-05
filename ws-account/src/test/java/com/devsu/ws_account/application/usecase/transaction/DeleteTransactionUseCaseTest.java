package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock
    private TransactionService service;

    private DeleteTransactionUseCase useCase;

    private UUID transactionId;

    @BeforeEach
    void setUp() {
        useCase = new DeleteTransactionUseCase(service);
        transactionId = UUID.randomUUID();
    }

    @Test
    void testDeleteTransactionSuccess() {
        doNothing().when(service).delete(any(UUID.class));

        assertDoesNotThrow(() -> useCase.delete(transactionId));

        verify(service, times(1)).delete(transactionId);
    }

    @Test
    void testDeleteTransactionDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(),
                "Error deleting transaction"
        );

        doThrow(dbException).when(service).delete(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.delete(transactionId));

        assertEquals(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error deleting transaction", exception.getMessage());
        verify(service, times(1)).delete(transactionId);
    }

    @Test
    void testDeleteTransactionCustomException() {
        CustomException customException = new CustomException(
                SPError.GENERIC_ERROR.getErrorCode(),
                "Custom error"
        );

        doThrow(customException).when(service).delete(any(UUID.class));

        CustomException exception = assertThrows(CustomException.class, () -> useCase.delete(transactionId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Custom error", exception.getMessage());
        verify(service, times(1)).delete(transactionId);
    }

    @Test
    void testDeleteTransactionGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        doThrow(genericException).when(service).delete(any(UUID.class));

        CustomException exception = assertThrows(CustomException.class, () -> useCase.delete(transactionId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during transaction deletion", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).delete(transactionId);
    }
}
