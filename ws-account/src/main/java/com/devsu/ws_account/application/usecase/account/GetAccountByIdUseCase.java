package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.application.port.in.account.GetAccountByIdPort;
import com.devsu.ws_account.application.port.out.AccountStorageRepository;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GetAccountByIdUseCase implements GetAccountByIdPort {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountByIdUseCase.class);
    private final AccountStorageRepository repository;

    public GetAccountByIdUseCase(AccountStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public AccountDomain getById(UUID id) {
        try {
            logger.info("Initiating process to retrieve account by ID: {}", id);
            AccountDomain account = repository.getById(id);
            logger.info("Successfully retrieved account with ID: {}", id);
            return account;
        } catch (DataBaseException d) {
            logger.error("Database error while retrieving account with ID: {}. Error: {}", id, d.getMessage());
            throw d;
        }catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving account with ID: {}. Error: {}", id, e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during account retrieval", e);
        }
    }
}
