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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllTransactionsUseCaseTest {

    @Mock
    private TransactionStorageRepository repository;

    private GetAllTransactionsUseCase useCase;

    private List<TransactionDomain> transactions;

    @BeforeEach
    void setUp() {
        useCase = new GetAllTransactionsUseCase(repository);
        transactions = List.of(
                TransactionDomain.builder().id(UUID.randomUUID()).build(),
                TransactionDomain.builder().id(UUID.randomUUID()).build()
        );
    }

    @Test
    void testGetAllTransactionsSuccess() {
        when(repository.getAll()).thenReturn(transactions);

        List<TransactionDomain> result = useCase.getAll();

        assertNotNull(result);
        assertEquals(transactions.size(), result.size());
        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllTransactionsDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                "Error retrieving transactions"
        );

        when(repository.getAll()).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.getAll());

        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error retrieving transactions", exception.getMessage());
        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllTransactionsGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getAll()).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAll());

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during transaction retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(repository, times(1)).getAll();
    }
}
