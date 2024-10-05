package com.devsu.ws_account.domain.service;

import com.devsu.ws_account.adapter.postgres.AccountPostgresAdapter;
import com.devsu.ws_account.adapter.postgres.TransactionPostgresAdapter;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.TransactionTypeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionPostgresAdapter transactionAdapter;

    @Mock
    private AccountPostgresAdapter accountAdapter;

    private TransactionService transactionService;

    private AccountDomain accountDomain;
    private TransactionDomain transactionDomain;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionAdapter, accountAdapter);

        accountDomain = new AccountDomain(UUID.randomUUID(), "1234567890", null, BigDecimal.valueOf(1000), true, UUID.randomUUID());
        TransactionTypeDomain transactionType = new TransactionTypeDomain(1, "DEPOSIT");
        transactionDomain = new TransactionDomain(UUID.randomUUID(), new Date(), transactionType, BigDecimal.valueOf(500), BigDecimal.valueOf(1500), accountDomain);
    }

    @Test
    void testCreateTransactionSuccess() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(null);
        when(transactionAdapter.save(any(TransactionDomain.class))).thenReturn(transactionDomain);

        TransactionDomain result = transactionService.create(transactionDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(accountAdapter, times(3)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, times(1)).save(any(TransactionDomain.class));
    }

    @Test
    void testCreateTransactionAccountNotFound() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> transactionService.create(transactionDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account does not exist", exception.getMessage());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, never()).save(any(TransactionDomain.class));
    }

    @Test
    void testCreateTransactionInsufficientFunds() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(transactionDomain);

        TransactionDomain withdrawalTransaction = TransactionDomain.builder()
                .id(UUID.randomUUID())
                .transactionType(new TransactionTypeDomain(2, "WITHDRAWAL"))
                .amount(BigDecimal.valueOf(2000))  // Attempt to withdraw more than the balance
                .account(accountDomain)
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> transactionService.create(withdrawalTransaction));

        assertEquals(SPError.BALANCE_NOT_AVAILABLE_FOR_TRANSACTION.getErrorCode(), exception.getErrorCode());
        assertEquals("Insufficient funds for this transaction", exception.getMessage());
        verify(accountAdapter, times(2)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, never()).save(any(TransactionDomain.class));
    }

    @Test
    void testUpdateTransactionSuccess() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(transactionDomain);
        when(transactionAdapter.update(any(TransactionDomain.class))).thenReturn(transactionDomain);

        TransactionDomain result = transactionService.update(transactionDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, times(1)).update(any(TransactionDomain.class));
    }

    @Test
    void testUpdateTransactionNotLastTransaction() {
        TransactionDomain otherTransaction = TransactionDomain.builder()
                .id(UUID.randomUUID())
                .transactionType(new TransactionTypeDomain(2, "WITHDRAWAL"))
                .amount(BigDecimal.valueOf(100))
                .account(accountDomain)
                .build();

        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(otherTransaction);

        CustomException exception = assertThrows(CustomException.class, () -> transactionService.update(transactionDomain));

        assertEquals(SPError.INVALID_TRANSACTION_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("This transaction cannot be modified/deleted as it is not the most recent one.", exception.getMessage());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, never()).update(any(TransactionDomain.class));
    }

    @Test
    void testDeleteTransactionSuccess() {
        when(transactionAdapter.getById(any(UUID.class))).thenReturn(transactionDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(transactionDomain);
        doNothing().when(transactionAdapter).delete(any(UUID.class));

        assertDoesNotThrow(() -> transactionService.delete(transactionDomain.getId()));
        verify(transactionAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteTransactionNotLastTransaction() {
        TransactionDomain otherTransaction = TransactionDomain.builder()
                .id(UUID.randomUUID())
                .transactionType(new TransactionTypeDomain(2, "WITHDRAWAL"))
                .amount(BigDecimal.valueOf(100))
                .account(accountDomain)
                .build();

        when(transactionAdapter.getById(any(UUID.class))).thenReturn(transactionDomain);
        when(transactionAdapter.getLastTransactionByAccount(any(AccountDomain.class))).thenReturn(otherTransaction);

        CustomException exception = assertThrows(CustomException.class, () -> transactionService.delete(transactionDomain.getId()));

        assertEquals(SPError.INVALID_TRANSACTION_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("This transaction cannot be modified/deleted as it is not the most recent one.", exception.getMessage());
        verify(transactionAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getLastTransactionByAccount(any(AccountDomain.class));
        verify(transactionAdapter, never()).delete(any(UUID.class));
    }

    @Test
    void testDeleteTransactionNotFound() {
        when(transactionAdapter.getById(any(UUID.class))).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> transactionService.delete(transactionDomain.getId()));

        assertEquals(SPError.INVALID_TRANSACTION_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("No transaction found.", exception.getMessage());
        verify(transactionAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, never()).delete(any(UUID.class));
    }
}
