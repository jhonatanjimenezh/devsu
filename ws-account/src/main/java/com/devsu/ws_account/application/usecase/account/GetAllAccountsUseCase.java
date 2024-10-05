package com.devsu.ws_account.application.usecase.account;

import com.devsu.ws_account.application.port.in.account.GetAllAccountsPort;
import com.devsu.ws_account.application.port.out.AccountStorageRepository;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetAllAccountsUseCase implements GetAllAccountsPort {

    private static final Logger logger = LoggerFactory.getLogger(GetAllAccountsUseCase.class);
    private final AccountStorageRepository repository;

    public GetAllAccountsUseCase(AccountStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AccountDomain> getAll() {
        try {
            logger.info("Starting retrieval process for all accounts.");
            List<AccountDomain> accounts = repository.getAll();
            logger.info("Successfully retrieved {} accounts.", accounts.size());
            return accounts;
        } catch (DataBaseException d) {
            logger.error("Database error occurred while retrieving all accounts. Error: {}", d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving all accounts. Error: {}", e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during account retrieval", e);
        }
    }
}
