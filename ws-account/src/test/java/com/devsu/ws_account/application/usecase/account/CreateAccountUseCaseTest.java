package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import com.devsu.ws_account.domain.service.AccountService;
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
class CreateAccountUseCaseTest {

    @Mock
    private AccountService service;

    private CreateAccountUseCase useCase;

    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        useCase = new CreateAccountUseCase(service);

        accountDomain = AccountDomain.builder()
                .id(UUID.randomUUID())
                .accountNumber("1234567890")
                .accountType(new AccountTypeDomain(1, "SAVINGS"))
                .initialBalance(BigDecimal.valueOf(1000))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();
    }

    @Test
    void testCreateAccountSuccess() {
        when(service.create(accountDomain)).thenReturn(accountDomain);

        AccountDomain result = useCase.create(accountDomain);

        assertNotNull(result);
        assertEquals(accountDomain.getAccountNumber(), result.getAccountNumber());
        verify(service, times(1)).create(accountDomain);
    }

    @Test
    void testCreateAccountDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(),
                "Account already exists"
        );

        when(service.create(accountDomain)).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.create(accountDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Account already exists", exception.getMessage());
        verify(service, times(1)).create(accountDomain);
    }

    @Test
    void testCreateAccountCustomException() {
        CustomException customException = new CustomException(
                SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(),
                "Invalid account details"
        );

        when(service.create(accountDomain)).thenThrow(customException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.create(accountDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Invalid account details", exception.getMessage());
        verify(service, times(1)).create(accountDomain);
    }

    @Test
    void testCreateAccountGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.create(accountDomain)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.create(accountDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during account creation", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).create(accountDomain);
    }
}
