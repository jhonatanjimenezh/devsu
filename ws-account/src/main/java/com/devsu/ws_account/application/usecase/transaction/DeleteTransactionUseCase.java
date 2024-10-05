package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.application.port.in.transaction.DeleteTransactionPort;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteTransactionUseCase implements DeleteTransactionPort {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTransactionUseCase.class);
    private final TransactionService service;

    public DeleteTransactionUseCase(TransactionService service) {
        this.service = service;
    }

    @Override
    public void delete(UUID transactionId) {
        try {
            logger.info("Initiating transaction deletion for transactionId: {}", transactionId);
            service.delete(transactionId);
            logger.info("Transaction with ID: {} deleted successfully", transactionId);

        } catch (CustomException c) {
            logger.error("Exception error occurred while attempting to delete transaction with ID: {}. Error: {}", transactionId, c.getMessage());
            throw c;
        } catch (DataBaseException d) {
            logger.error("Database error occurred while attempting to delete transaction with ID: {}. Error: {}", transactionId, d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while attempting to delete transaction with ID: {}. Error: {}", transactionId, e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during transaction deletion", e);
        }
    }
}
