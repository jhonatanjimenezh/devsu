package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.adapter.postgres.models.TransactionEntity;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.TransactionTypeDomain;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionPostgresAdapterTest {

    @Autowired
    private TransactionPostgresAdapter adapter;

    @MockBean
    private TransactionPostgresRepository repository;

    private TransactionDomain transactionDomain;
    private TransactionEntity transactionEntity;

    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        accountDomain = new AccountDomain(UUID.randomUUID(), "1234567890", new AccountTypeDomain(1, "OTHER"), BigDecimal.valueOf(1000), true, UUID.randomUUID());
        transactionDomain = new TransactionDomain(UUID.randomUUID(), new Date(), new TransactionTypeDomain(1, "DEPOSIT"), BigDecimal.valueOf(500), BigDecimal.valueOf(1500), accountDomain);
        transactionEntity = TransactionEntity.fromDomain(transactionDomain);
    }

    @Test
    void testSaveTransactionSuccess() {
        when(repository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);

        TransactionDomain result = adapter.save(transactionDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getAmount(), result.getAmount());
        verify(repository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void testSaveTransactionFailure() {
        when(repository.save(any(TransactionEntity.class))).thenThrow(new RuntimeException("Error saving transaction"));

        assertThrows(DataBaseException.class, () -> adapter.save(transactionDomain));
        verify(repository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void testGetTransactionByIdSuccess() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(transactionEntity));

        TransactionDomain result = adapter.getById(transactionDomain.getId());

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testGetTransactionByIdFailure() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(DataBaseException.class, () -> adapter.getById(transactionDomain.getId()));
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveTransactionReturnsNull() {
        when(repository.save(any(TransactionEntity.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.save(transactionDomain));
        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void testGetTransactionByIdException() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.getById(transactionDomain.getId()));
        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testDeleteTransactionSuccess() {
        doNothing().when(repository).deleteById(any(UUID.class));

        assertDoesNotThrow(() -> adapter.delete(transactionDomain.getId()));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testDeleteTransactionFailure() {
        doThrow(new RuntimeException("Error deleting transaction")).when(repository).deleteById(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.delete(transactionDomain.getId()));
        assertEquals(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testGetAllPaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<TransactionEntity> page = mock(Page.class);
        when(repository.findAll(pageable)).thenReturn(page);

        when(page.map(any())).thenAnswer(invocation -> {
            return Page.empty();
        });

        Page<TransactionDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllPaginatedWithData() {
        Pageable pageable = PageRequest.of(0, 10);
        TransactionEntity transactionEntity = TransactionEntity.fromDomain(transactionDomain);

        Page<TransactionEntity> page = new PageImpl<>(List.of(transactionEntity));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<TransactionDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionDomain.getId(), result.getContent().get(0).getId());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void testGetLastTransactionByAccountSuccess() {
        when(repository.findTopByAccountOrderByDateDesc(any(AccountEntity.class))).thenReturn(transactionEntity);

        TransactionDomain result = adapter.getLastTransactionByAccount(accountDomain);

        assertNotNull(result);
        assertEquals(transactionDomain.getId(), result.getId());
        verify(repository, times(1)).findTopByAccountOrderByDateDesc(any(AccountEntity.class));
    }

    @Test
    void testGetLastTransactionByAccountNotFound() {
        when(repository.findTopByAccountOrderByDateDesc(any(AccountEntity.class))).thenReturn(null);

        TransactionDomain result = adapter.getLastTransactionByAccount(accountDomain);

        assertNull(result);
        verify(repository, times(1)).findTopByAccountOrderByDateDesc(any(AccountEntity.class));
    }

    @Test
    void testGetTransactionsByAccountSuccess() {
        when(repository.findByAccount(any(AccountEntity.class))).thenReturn(List.of(transactionEntity));

        List<TransactionDomain> result = adapter.getTransactionsByAccount(accountDomain);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(transactionDomain.getId(), result.get(0).getId());
        verify(repository, times(1)).findByAccount(any(AccountEntity.class));
    }

    @Test
    void testGetTransactionsByAccountNotFound() {
        when(repository.findByAccount(any(AccountEntity.class))).thenReturn(List.of());

        List<TransactionDomain> result = adapter.getTransactionsByAccount(accountDomain);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByAccount(any(AccountEntity.class));
    }
}
