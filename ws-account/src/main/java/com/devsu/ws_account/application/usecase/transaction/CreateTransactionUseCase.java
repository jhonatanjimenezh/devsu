package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.application.port.in.transaction.CreateTransactionPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTransactionUseCase implements CreateTransactionPort {

    private static final Logger logger = LoggerFactory.getLogger(CreateTransactionUseCase.class);
    private final TransactionService service;

    public CreateTransactionUseCase(TransactionService service) {
        this.service = service;
    }

    @Override
    public TransactionDomain create(TransactionDomain domain) {
        try {
            logger.info("Initiating transaction creation for accountNumber: {} with amount: {}", domain.getAccount().getAccountNumber(), domain.getAmount());
            TransactionDomain createdTransaction = service.create(domain);
            logger.info("Transaction created successfully for accountNumber: {} with transaction ID: {}", domain.getAccount().getAccountNumber(), createdTransaction.getId());
            return createdTransaction;
        } catch (DataBaseException d) {
            logger.error("Database error while attempting to create transaction for accountNumber: {}. Error: {}", domain.getAccount().getAccountNumber(), d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error during transaction creation for accountNumber: {}. Error: {}", domain.getAccount().getAccountNumber(), e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during transaction creation", e);
        }
    }
}
