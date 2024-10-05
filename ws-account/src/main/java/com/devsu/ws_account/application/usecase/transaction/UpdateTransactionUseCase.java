package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.application.port.in.transaction.UpdateTransactionPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateTransactionUseCase implements UpdateTransactionPort {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTransactionUseCase.class);
    private final TransactionService service;

    public UpdateTransactionUseCase(TransactionService service) {
        this.service = service;
    }

    @Override
    public TransactionDomain update(TransactionDomain domain) {
        try {
            logger.info("Starting transaction update process for transactionId: {} with amount: {}", domain.getId(), domain.getAmount());
            TransactionDomain updatedTransaction = service.update(domain);
            logger.info("Transaction successfully updated for transactionId: {}", updatedTransaction.getId());
            return updatedTransaction;
        } catch (CustomException c) {
            logger.error("Business validation error while attempting to update transaction for transactionId: {}. Error: {}",
                    domain.getId(), c.getMessage());
            throw c;
        } catch (DataBaseException d) {
            logger.error("Database error while attempting to update transaction for transactionId: {}. Error: {}",
                    domain.getId(), d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error during transaction update for transactionId: {}. Error: {}",
                    domain.getId(), e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during transaction update", e);
        }
    }
}
