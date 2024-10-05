package com.devsu.ws_account.application.usecase.transaction;

import com.devsu.ws_account.application.port.in.transaction.GetAllTransactionsPort;
import com.devsu.ws_account.application.port.out.TransactionStorageRepository;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.TransactionDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllTransactionsUseCase implements GetAllTransactionsPort {

    private static final Logger logger = LoggerFactory.getLogger(GetAllTransactionsUseCase.class);
    private final TransactionStorageRepository repository;

    public GetAllTransactionsUseCase(TransactionStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransactionDomain> getAll() {
        try {
            logger.info("Starting retrieval process for all transactions.");
            List<TransactionDomain> transactions = this.repository.getAll();
            logger.info("Successfully retrieved {} transactions.", transactions.size());
            return transactions;
        } catch (DataBaseException d) {
            logger.error("Database error occurred while retrieving all transactions. Error: {}", d.getMessage());
            throw d;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving all transactions. Error: {}", e.getMessage(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "An unexpected error occurred during transaction retrieval", e);
        }
    }
}
