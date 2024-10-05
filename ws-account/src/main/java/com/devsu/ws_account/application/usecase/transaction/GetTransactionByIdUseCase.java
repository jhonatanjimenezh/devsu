package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.application.port.in.transaction.GetTransactionByIdPort;
import com.devsu.ws_account.application.port.out.TransactionStorageRepository;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetTransactionByIdUseCase implements GetTransactionByIdPort {

    private static final Logger logger = LoggerFactory.getLogger(GetTransactionByIdUseCase.class);
    private final TransactionStorageRepository repository;

    public GetTransactionByIdUseCase(TransactionStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransactionDomain getById(UUID id) {
        try {
            logger.info("Initiating process to retrieve transaction by ID: {}", id);
            TransactionDomain transaction = this.repository.getById(id);
            logger.info("Successfully retrieved transaction with ID: {}", id);
            return transaction;
        } catch (CustomException c) {
            logger.error("Business validation error while attempting to update transaction for transactionId: {}. Error: {}",
                    id, c.getMessage());
            throw c;
        } catch (DataBaseException d) {
            logger.error("Database error while retrieving transaction with ID: {}. Error: {}", id, d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving transaction with ID: {}. Error: {}", id, e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during transaction retrieval", e);
        }
    }
}
