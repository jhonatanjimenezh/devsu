package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.adapter.postgres.models.TransactionEntity;
import com.devsu.ws_account.application.port.out.TransactionStorageRepository;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransactionPostgresAdapter implements TransactionStorageRepository {

    private static final Logger logger = LoggerFactory.getLogger(TransactionPostgresAdapter.class);
    private final TransactionPostgresRepository repository;

    public TransactionPostgresAdapter(TransactionPostgresRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TransactionDomain save(TransactionDomain domain) {
        try {
            logger.debug("Attempting to save transaction: {}", domain);
            TransactionEntity entity = TransactionEntity.fromDomain(domain);
            TransactionEntity savedEntity = repository.save(entity);
            logger.info("Transaction saved successfully with ID: {}", savedEntity.getId());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error saving transaction [{}]: {}", domain.toString(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDomain> getAll() {
        try {
            logger.debug("Retrieving all transactions from the database");
            List<TransactionDomain> transactions = repository.findAll().stream()
                    .map(TransactionEntity::toDomain)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} transactions from the database", transactions.size());
            return transactions;
        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDomain getById(UUID id) {
        try {
            logger.debug("Retrieving transaction by ID: {}", id);
            TransactionDomain transaction = repository.findById(id)
                    .map(TransactionEntity::toDomain)
                    .orElseThrow(() -> {
                        logger.warn("Transaction not found with ID: {}", id);
                        return new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), "Transaction not found");
                    });
            logger.info("Transaction retrieved successfully with ID: {}", id);
            return transaction;
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving transaction by ID [{}]: {}", id, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDomain> getAllPaginated(int page, int size) {
        try {
            logger.debug("Retrieving transactions paginated - Page: {}, Size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionDomain> transactionsPage = repository.findAll(pageable)
                    .map(TransactionEntity::toDomain);
            logger.info("Retrieved {} transactions on page {}", transactionsPage.getNumberOfElements(), page);
            return transactionsPage;
        } catch (Exception e) {
            logger.error("Error retrieving paginated transactions - Page: {}, Size: {}: {}", page, size, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public TransactionDomain update(TransactionDomain domain) {
        try {
            logger.debug("Attempting to update transaction with ID: {}", domain.getId());
            if (!repository.existsById(domain.getId())) {
                logger.warn("Transaction not found for update with ID: {}", domain.getId());
                throw new DataBaseException(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), "Transaction not found");
            }
            TransactionEntity entity = TransactionEntity.fromDomain(domain);
            TransactionEntity updatedEntity = repository.save(entity);
            logger.info("Transaction updated successfully with ID: {}", updatedEntity.getId());
            return updatedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error updating transaction [{}]: {}", domain.getId(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            logger.debug("Attempting to delete transaction by ID: {}", id);
            repository.deleteById(id);
            logger.info("Transaction deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting transaction [{}]: {}", id, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<TransactionDomain> getTransactionsByAccount(AccountDomain account) {
        try {
            logger.debug("Retrieving transactions for account ID: {}", account.getId());

            List<TransactionDomain> transactions = repository.findByAccount(AccountEntity.fromDomain(account)).stream()
                    .map(TransactionEntity::toDomain)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} transactions for account ID: {}", transactions.size(), account.getId());
            return transactions;
        } catch (Exception e) {
            logger.error("Error retrieving transactions for account ID [{}]: {}", account.getId(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public TransactionDomain getLastTransactionByAccount(AccountDomain account) {
        try {
            logger.debug("Retrieving last transaction for account ID: {}", account.getId());
            TransactionEntity lastTransactionEntity = repository.findTopByAccountOrderByDateDesc(AccountEntity.fromDomain(account));

            if (lastTransactionEntity == null) {
                logger.info("No transactions found for account ID: {}", account.getId());
                return null;
            }

            logger.info("Last transaction for account ID: {} retrieved successfully", account.getId());
            return lastTransactionEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error retrieving last transaction for account ID [{}]: {}", account.getId(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }


}
