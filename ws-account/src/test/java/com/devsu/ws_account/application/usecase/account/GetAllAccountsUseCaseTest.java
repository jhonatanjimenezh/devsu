package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.application.port.out.AccountStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllAccountsUseCaseTest {

    @Mock
    private AccountStorageRepository repository;

    private GetAllAccountsUseCase useCase;

    private List<AccountDomain> accountList;

    @BeforeEach
    void setUp() {
        useCase = new GetAllAccountsUseCase(repository);

        AccountDomain account1 = AccountDomain.builder()
                .id(UUID.randomUUID())
                .accountNumber("1234567890")
                .accountType(null) // You can replace this with a proper AccountTypeDomain object if needed
                .initialBalance(BigDecimal.valueOf(1000))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();

        AccountDomain account2 = AccountDomain.builder()
                .id(UUID.randomUUID())
                .accountNumber("0987654321")
                .accountType(null) // You can replace this with a proper AccountTypeDomain object if needed
                .initialBalance(BigDecimal.valueOf(2000))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();

        accountList = List.of(account1, account2);
    }

    @Test
    void testGetAllAccountsSuccess() {
        when(repository.getAll()).thenReturn(accountList);

        List<AccountDomain> result = useCase.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllAccountsDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                "Error retrieving accounts"
        );

        when(repository.getAll()).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.getAll());

        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error retrieving accounts", exception.getMessage());
        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllAccountsGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getAll()).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getAll());

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during account retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(repository, times(1)).getAll();
    }
}
