package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.application.port.in.account.CreateAccountPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAccountUseCase implements CreateAccountPort {

    private static final Logger logger = LoggerFactory.getLogger(CreateAccountUseCase.class);
    private final AccountService service;

    public CreateAccountUseCase(AccountService service) {
        this.service = service;
    }

    public AccountDomain create(AccountDomain domain) {
        try {
            logger.info("Initiating account creation for clientId: {} with account number: {}", domain.getClientId(), domain.getAccountNumber());
            AccountDomain createdAccount = service.create(domain);
            logger.info("Account created successfully for clientId: {} with account ID: {}", domain.getClientId(), createdAccount.getId());
            return createdAccount;
        } catch (CustomException c) {
            logger.error("Business validation error while attempting to update account for clientId: {}. Error: {}",
                    domain.getClientId(), c.getMessage());
            throw c;
        }  catch (DataBaseException d) {
            logger.error("Database error while attempting to create account for clientId: {}. Error: {}", domain.getClientId(), d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error during account creation for clientId: {}. Error: {}", domain.getClientId(), e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during account creation", e);
        }
    }
}
