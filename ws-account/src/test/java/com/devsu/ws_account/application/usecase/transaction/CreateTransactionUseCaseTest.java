package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private TransactionService service;

    private CreateTransactionUseCase useCase;

    private TransactionDomain transactionDomain;
    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        useCase = new CreateTransactionUseCase(service);

        accountDomain = AccountDomain.builder()
                .id(UUID.randomUUID())
                .accountNumber("1234567890")
                .initialBalance(BigDecimal.valueOf(1000))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();

        transactionDomain = TransactionDomain.builder()
                .id(UUID.randomUUID())
                .date(new Date())
                .amount(BigDecimal.valueOf(500))
                .balance(BigDecimal.valueOf(1500))
                .account(accountDomain)
                .build();
    }

    @Test
    void testCreateTransactionSuccess() {
        when(service.create(any(TransactionDomain.class))).thenReturn(transactionDomain);

        TransactionDomain result = useCase.create(transactionDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(service, times(1)).create(transactionDomain);
    }

    @Test
    void testCreateTransactionDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(),
                "Error saving transaction"
        );

        when(service.create(any(TransactionDomain.class))).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.create(transactionDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error saving transaction", exception.getMessage());
        verify(service, times(1)).create(transactionDomain);
    }

    @Test
    void testCreateTransactionGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.create(any(TransactionDomain.class))).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.create(transactionDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during transaction creation", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).create(transactionDomain);
    }
}
