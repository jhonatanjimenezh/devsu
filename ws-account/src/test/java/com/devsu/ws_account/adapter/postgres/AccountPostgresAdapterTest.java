package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountPostgresAdapterTest {

    @Autowired
    private AccountPostgresAdapter adapter;

    @MockBean
    private AccountPostgresRepository repository;

    private AccountDomain accountDomain;
    private AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        AccountTypeDomain accountType = new AccountTypeDomain(1, "SAVINGS");
        accountDomain = new AccountDomain(UUID.randomUUID(), "1234567890", accountType, BigDecimal.valueOf(1000), true, UUID.randomUUID());
        accountEntity = AccountEntity.fromDomain(accountDomain);
    }

    @Test
    void testSaveAccountSuccess() {
        when(repository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        AccountDomain result = adapter.save(accountDomain);

        assertNotNull(result);
        assertEquals(accountDomain.getAccountNumber(), result.getAccountNumber());
        verify(repository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    void testSaveAccountFailure() {
        when(repository.save(any(AccountEntity.class))).thenThrow(new RuntimeException("Error saving account"));

        assertThrows(DataBaseException.class, () -> adapter.save(accountDomain));
        verify(repository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    void testGetAccountByIdSuccess() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(accountEntity));

        AccountDomain result = adapter.getById(accountDomain.getId());

        assertNotNull(result);
        assertEquals(accountDomain.getAccountNumber(), result.getAccountNumber());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testGetAccountByIdFailure() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(DataBaseException.class, () -> adapter.getById(accountDomain.getId()));
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveAccountReturnsNull() {
        when(repository.save(any(AccountEntity.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.save(accountDomain));
        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    void testGetAccountByIdException() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.getById(accountDomain.getId()));
        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateAccountNotFound() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.update(accountDomain));
        assertEquals(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account not found", exception.getMessage());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, never()).save(any(AccountEntity.class));
    }

    @Test
    void testDeleteAccountSuccess() {
        doNothing().when(repository).deleteById(any(UUID.class));

        assertDoesNotThrow(() -> adapter.delete(accountDomain.getId()));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testDeleteAccountFailure() {
        doThrow(new RuntimeException("Error deleting account")).when(repository).deleteById(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.delete(accountDomain.getId()));
        assertEquals(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testFindByAccountNumberNotFound() {
        when(repository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        AccountDomain result = adapter.findByAccountNumber("1234567890");

        assertNull(result);
        verify(repository, times(1)).findByAccountNumber(anyString());
    }

    @Test
    void testGetAllPaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AccountEntity> page = mock(Page.class);
        when(repository.findAll(pageable)).thenReturn(page);

        when(page.map(any())).thenAnswer(invocation -> {
            return Page.empty();
        });

        Page<AccountDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllPaginatedWithData() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountEntity accountEntity = AccountEntity.fromDomain(accountDomain);

        Page<AccountEntity> page = new PageImpl<>(List.of(accountEntity));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<AccountDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(accountDomain.getAccountNumber(), result.getContent().get(0).getAccountNumber());
        verify(repository, times(1)).findAll(pageable);
    }
}
