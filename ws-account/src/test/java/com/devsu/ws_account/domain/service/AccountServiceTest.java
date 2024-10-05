package com.devsu.ws_account.domain.service;

import com.devsu.ws_account.adapter.postgres.AccountPostgresAdapter;
import com.devsu.ws_account.adapter.postgres.TransactionPostgresAdapter;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountPostgresAdapter accountAdapter;

    @Mock
    private TransactionPostgresAdapter transactionAdapter;

    private AccountService accountService;

    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountAdapter, transactionAdapter);

        AccountTypeDomain accountType = new AccountTypeDomain(1, "SAVINGS");
        accountDomain = new AccountDomain(UUID.randomUUID(), "1234567890", accountType, BigDecimal.valueOf(1000), true, UUID.randomUUID());
    }

    @Test
    void testCreateAccountSuccess() {
        when(accountAdapter.findByAccountNumber(anyString())).thenReturn(null);
        when(accountAdapter.save(any(AccountDomain.class))).thenReturn(accountDomain);

        AccountDomain result = accountService.create(accountDomain);

        assertNotNull(result);
        assertEquals(accountDomain.getAccountNumber(), result.getAccountNumber());
        verify(accountAdapter, times(1)).findByAccountNumber(anyString());
        verify(accountAdapter, times(1)).save(any(AccountDomain.class));
    }

    @Test
    void testCreateAccountAlreadyExists() {
        when(accountAdapter.findByAccountNumber(anyString())).thenReturn(accountDomain);

        CustomException exception = assertThrows(CustomException.class, () -> accountService.create(accountDomain));

        assertEquals(SPError.UNCHANGEABLE_ACCOUNT_DATA.getErrorCode(), exception.getErrorCode());
        assertEquals("Account already exists", exception.getMessage());
        verify(accountAdapter, times(1)).findByAccountNumber(anyString());
        verify(accountAdapter, never()).save(any(AccountDomain.class));
    }


    @Test
    void testUpdateAccountSuccess() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(accountAdapter.update(any(AccountDomain.class))).thenReturn(accountDomain);

        AccountDomain result = accountService.update(accountDomain);

        assertNotNull(result);
        assertEquals(accountDomain.getId(), result.getId());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(accountAdapter, times(1)).update(any(AccountDomain.class));
    }

    @Test
    void testUpdateAccountNotFound() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> accountService.update(accountDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account with ID " + accountDomain.getId() + " does not exist", exception.getMessage());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(accountAdapter, never()).update(any(AccountDomain.class));
    }

    @Test
    void testDeleteAccountSuccess() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getTransactionsByAccount(any(AccountDomain.class))).thenReturn(List.of());

        assertDoesNotThrow(() -> accountService.delete(accountDomain.getId()));
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getTransactionsByAccount(any(AccountDomain.class));
        verify(accountAdapter, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteAccountWithTransactions() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getTransactionsByAccount(any(AccountDomain.class))).thenReturn(List.of());

        assertDoesNotThrow(() -> accountService.delete(accountDomain.getId()));
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getTransactionsByAccount(any(AccountDomain.class));
        verify(accountAdapter, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteAccountNotFound() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> accountService.delete(accountDomain.getId()));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account with ID " + accountDomain.getId() + " does not exist", exception.getMessage());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, never()).getTransactionsByAccount(any(AccountDomain.class));
        verify(accountAdapter, never()).delete(any(UUID.class));
    }

    @Test
    void testDeleteAccountWithTransactionsFailure() {
        when(accountAdapter.getById(any(UUID.class))).thenReturn(accountDomain);
        when(transactionAdapter.getTransactionsByAccount(any(AccountDomain.class))).thenReturn(List.of());
        doThrow(new DataBaseException(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), "Error deleting account")).when(accountAdapter).delete(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> accountService.delete(accountDomain.getId()));

        assertEquals(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error deleting account", exception.getMessage());
        verify(accountAdapter, times(1)).getById(any(UUID.class));
        verify(transactionAdapter, times(1)).getTransactionsByAccount(any(AccountDomain.class));
        verify(accountAdapter, times(1)).delete(any(UUID.class));
    }
}
