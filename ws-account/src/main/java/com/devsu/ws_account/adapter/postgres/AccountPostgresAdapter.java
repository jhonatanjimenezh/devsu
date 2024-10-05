package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.application.port.out.AccountStorageRepository;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
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
public class AccountPostgresAdapter implements AccountStorageRepository {

    private static final Logger logger = LoggerFactory.getLogger(AccountPostgresAdapter.class);
    private final AccountPostgresRepository repository;

    public AccountPostgresAdapter(AccountPostgresRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AccountDomain save(AccountDomain domain) {
        try {
            logger.debug("Attempting to save account: {}", domain);
            AccountEntity entity = AccountEntity.fromDomain(domain);
            AccountEntity savedEntity = repository.save(entity);
            logger.info("Account saved successfully with ID: {}", savedEntity.getId());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error saving account [{}]: {}", domain.getAccountNumber(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDomain> getAll() {
        try {
            logger.debug("Retrieving all accounts from the database");
            List<AccountDomain> accounts = repository.findAll().stream()
                    .map(AccountEntity::toDomain)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} accounts from the database", accounts.size());
            return accounts;
        } catch (Exception e) {
            logger.error("Error retrieving all accounts: {}", e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDomain getById(UUID id) {
        try {
            logger.debug("Retrieving account by ID: {}", id);
            AccountDomain account = repository.findById(id)
                    .map(AccountEntity::toDomain)
                    .orElseThrow(() -> {
                        logger.warn("Account not found with ID: {}", id);
                        return new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(), "Account not found");
                    });
            logger.info("Account retrieved successfully with ID: {}", id);
            return account;
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving account by ID [{}]: {}", id, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDomain> getAllPaginated(int page, int size) {
        try {
            logger.debug("Retrieving accounts paginated - Page: {}, Size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<AccountDomain> accountsPage = repository.findAll(pageable)
                    .map(AccountEntity::toDomain);
            logger.info("Retrieved {} accounts on page {}", accountsPage.getNumberOfElements(), page);
            return accountsPage;
        } catch (Exception e) {
            logger.error("Error retrieving paginated accounts - Page: {}, Size: {}: {}", page, size, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public AccountDomain update(AccountDomain domain) {
        try {
            logger.debug("Attempting to update account with ID: {}", domain.getId());
            if (!repository.existsById(domain.getId())) {
                logger.warn("Account not found for update with ID: {}", domain.getId());
                throw new DataBaseException(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(), "Account not found");
            }
            AccountEntity entity = AccountEntity.fromDomain(domain);
            AccountEntity updatedEntity = repository.save(entity);
            logger.info("Account updated successfully with ID: {}", updatedEntity.getId());
            return updatedEntity.toDomain();
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating account [{}]: {}", domain.getId(), e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_UPDATE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            logger.debug("Attempting to delete account by ID: {}", id);
            repository.deleteById(id);
            logger.info("Account deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting account [{}]: {}", id, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_DELETE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDomain findByAccountNumber(String accountNumber) {
        try {
            logger.debug("Retrieving account by account number: {}", accountNumber);
            AccountDomain account = repository.findByAccountNumber(accountNumber)
                    .map(AccountEntity::toDomain)
                    .orElse(null);
            if (account != null) {
                logger.info("Account retrieved successfully with account number: {}", accountNumber);
            } else {
                logger.warn("Account not found with account number: {}", accountNumber);
            }
            return account;
        } catch (Exception e) {
            logger.error("Error retrieving account by account number [{}]: {}", accountNumber, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDomain findByClientId(UUID clientId) {
        try {
            logger.debug("Retrieving accounts by client ID: {}", clientId);
            List<AccountDomain> accounts = repository.findByClientId(clientId).stream()
                    .map(AccountEntity::toDomain)
                    .collect(Collectors.toList());
            if (!accounts.isEmpty()) {
                logger.info("Retrieved {} accounts for client ID: {}", accounts.size(), clientId);
                return accounts.get(0);
            } else {
                logger.warn("No accounts found for client ID: {}", clientId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving accounts by client ID [{}]: {}", clientId, e.getMessage(), e);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorCode(),
                    SPError.ACCOUNT_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

}
