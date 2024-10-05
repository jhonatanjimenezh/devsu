package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.application.port.in.account.DeleteAccountPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DeleteAccountUseCase implements DeleteAccountPort {

    private static final Logger logger = LoggerFactory.getLogger(DeleteAccountUseCase.class);

    private final AccountService service;

    public DeleteAccountUseCase(AccountService service) {
        this.service = service;
    }

    @Override
    public void delete(UUID accountId) {
        try {
            logger.info("Initiating account deletion for accountId: {}", accountId);
            service.delete(accountId);
            logger.info("Account with ID: {} deleted successfully", accountId);

        } catch (DataBaseException d) {
            logger.error("Database error occurred while attempting to delete account with ID: {}. Error: {}", accountId, d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while attempting to delete account with ID: {}. Error: {}", accountId, e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during account deletion", e);
        }
    }
}
