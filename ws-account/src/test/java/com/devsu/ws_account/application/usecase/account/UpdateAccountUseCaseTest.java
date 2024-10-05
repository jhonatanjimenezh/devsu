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
class UpdateAccountUseCaseTest {

    @Mock
    private AccountService service;

    private UpdateAccountUseCase useCase;

    private AccountDomain accountDomain;

    @BeforeEach
    void setUp() {
        useCase = new UpdateAccountUseCase(service);

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
    void testUpdateAccountSuccess() {
        when(service.update(accountDomain)).thenReturn(accountDomain);

        AccountDomain result = useCase.update(accountDomain);

        assertNotNull(result);
        assertEquals(accountDomain.getAccountNumber(), result.getAccountNumber());
        verify(service, times(1)).update(accountDomain);
    }

    @Test
    void testUpdateAccountDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(),
                "Failed to update account"
        );

        when(service.update(accountDomain)).thenThrow(dbException);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.update(accountDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Failed to update account", exception.getMessage());
        verify(service, times(1)).update(accountDomain);
    }

    @Test
    void testUpdateAccountCustomException() {
        CustomException customException = new CustomException(
                SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(),
                "Invalid account details"
        );

        when(service.update(accountDomain)).thenThrow(customException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.update(accountDomain));

        assertEquals(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Invalid account details", exception.getMessage());
        verify(service, times(1)).update(accountDomain);
    }

    @Test
    void testUpdateAccountGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        when(service.update(accountDomain)).thenThrow(genericException);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.update(accountDomain));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during account update", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).update(accountDomain);
    }
}
