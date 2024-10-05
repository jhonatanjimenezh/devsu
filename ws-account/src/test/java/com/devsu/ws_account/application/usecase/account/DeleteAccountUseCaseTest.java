package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseTest {

    @Mock
    private AccountService service;

    private DeleteAccountUseCase useCase;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        useCase = new DeleteAccountUseCase(service);
        accountId = UUID.randomUUID();
    }

    @Test
    void testDeleteAccountSuccess() {
        // No exceptions thrown, implying success.
        doNothing().when(service).delete(accountId);

        assertDoesNotThrow(() -> useCase.delete(accountId));

        verify(service, times(1)).delete(accountId);
    }

    @Test
    void testDeleteAccountDataBaseException() {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(),
                "Error deleting account"
        );

        doThrow(dbException).when(service).delete(accountId);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> useCase.delete(accountId));

        assertEquals(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error deleting account", exception.getMessage());
        verify(service, times(1)).delete(accountId);
    }

    @Test
    void testDeleteAccountGenericException() {
        RuntimeException genericException = new RuntimeException("Unexpected error");

        doThrow(genericException).when(service).delete(accountId);

        CustomException exception = assertThrows(CustomException.class, () -> useCase.delete(accountId));

        assertEquals(SPError.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("An unexpected error occurred during account deletion", exception.getMessage());
        assertSame(genericException, exception.getCause());
        verify(service, times(1)).delete(accountId);
    }
}
