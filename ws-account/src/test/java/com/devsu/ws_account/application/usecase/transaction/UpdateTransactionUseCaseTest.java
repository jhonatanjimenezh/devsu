package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
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
class UpdateTransactionUseCaseTest {

    @Mock
    private TransactionService service;

    private UpdateTransactionUseCase useCase;

    private TransactionDomain transactionDomain;

    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        useCase = new UpdateTransactionUseCase(service);

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
    void testUpdateTransactionSuccess() {
        when(service.update(any(TransactionDomain.class))).thenReturn(transactionDomain);

        TransactionDomain result = useCase.update(transactionDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(service, times(1)).update(transactionDomain);
    }

    @Test
    void testUpdateTransactionDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(),
                "Error updating transaction"
        );

        when(service.update(any(TransactionDomain.class))).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.update(transactionDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error updating transaction", exception.getMessage());
        verify(service, times(1)).update(transactionDomain);
    }

    @Test
    void testUpdateTransactionGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.update(any(TransactionDomain.class))).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.update(transactionDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during transaction update", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).update(transactionDomain);
    }
}
