package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.application.port.in.account.UpdateAccountPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateAccountUseCase implements UpdateAccountPort {

    private static final Logger logger = LoggerFactory.getLogger(UpdateAccountUseCase.class);
    private final AccountService service;

    public UpdateAccountUseCase(AccountService service) {
        this.service = service;
    }

    @Override
    public AccountDomain update(AccountDomain domain) {
        try {
            logger.info("Starting account update process for clientId: {} with account number: {}", domain.getClientId(), domain.getAccountNumber());
            AccountDomain updatedAccount = service.update(domain);
            logger.info("Account successfully updated for clientId: {} with account ID: {}", domain.getClientId(), updatedAccount.getId());
            return updatedAccount;
        } catch (CustomException c) {
            logger.error("Business validation error while attempting to update account for clientId: {}. Error: {}",
                    domain.getClientId(), c.getMessage());
            throw c;
        } catch (DataBaseException d) {
            logger.error("Database error while attempting to update account for clientId: {}. Error: {}",
                    domain.getClientId(), d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error during account update for clientId: {}. Error: {}",
                    domain.getClientId(), e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during account update", e);
        }
    }
}
