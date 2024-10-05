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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountByIdUseCaseTest {

    @Mock
    private AccountStorageRepository repository;

    private GetAccountByIdUseCase useCase;

    private AccountDomain accountDomain;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        useCase = new GetAccountByIdUseCase(repository);

        accountId = UUID.randomUUID();
        accountDomain = AccountDomain.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(null) // You can replace this with a proper AccountTypeDomain object if needed
                .initialBalance(BigDecimal.valueOf(1000))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();
    }

    @Test
    void testGetAccountByIdSuccess() {
        when(repository.getById(accountId)).thenReturn(accountDomain);

        AccountDomain result = useCase.getById(accountId);

        assertNotNull(result);
        assertEquals(accountDomain.getId(), result.getId());
        verify(repository, times(1)).getById(accountId);
    }

    @Test
    void testGetAccountByIdDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                "Account not found"
        );

        when(repository.getById(accountId)).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.getById(accountId));

        assertEquals(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account not found", exception.getMessage());
        verify(repository, times(1)).getById(accountId);
    }

    @Test
    void testGetAccountByIdGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(repository.getById(accountId)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.getById(accountId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during account retrieval", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(repository, times(1)).getById(accountId);
    }
}
