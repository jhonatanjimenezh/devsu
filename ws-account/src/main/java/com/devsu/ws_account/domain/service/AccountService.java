package com.devsu.ws_account.domain.service;

import com.devsu.ws_account.adapter.postgres.AccountPostgresAdapter;
import com.devsu.ws_account.adapter.postgres.TransactionPostgresAdapter;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Optional;

public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private static final String ACCOUNT_NOT_FOUND = "Account with ID %s does not exist";

    private final AccountPostgresAdapter accountAdapter;
    private final TransactionPostgresAdapter transactionAdapter;

    public AccountService(AccountPostgresAdapter accountAdapter, TransactionPostgresAdapter transactionAdapter) {
        this.accountAdapter = accountAdapter;
        this.transactionAdapter = transactionAdapter;
    }

    @Transactional
    public AccountDomain create(AccountDomain accountDomain) {
        logger.info("Starting account creation process for clientId: {}", accountDomain.getClientId());

        if (accountExists(accountDomain.getAccountNumber())) {
            logAndThrowError("Account creation failed: Client ID {} already has an account with number {} and type {}",
                    accountDomain.getClientId(), accountDomain.getAccountNumber(), accountDomain.getAccountType(),
                    SPError.ACCOUNT_ADAPTER_SAVE_ERROR, "Account already exists");
        }

        accountDomain.setStatus(true);
        AccountDomain createdAccount = accountAdapter.save(accountDomain);
        logger.info("Account successfully created with ID: {} for clientId: {}", createdAccount.getId(), accountDomain.getClientId());

        return createdAccount;
    }

    @Transactional
    public AccountDomain update(AccountDomain accountDomain) {
        logger.info("Starting account update process for accountId: {}", accountDomain.getId());

        AccountDomain existingAccount = getAccountOrThrow(accountDomain.getId());
        validateNonModifiableFields(accountDomain, existingAccount);

        AccountDomain updatedAccount = accountAdapter.update(accountDomain);
        logger.info("Account successfully updated with ID: {} for clientId: {}", updatedAccount.getId(), accountDomain.getClientId());

        return updatedAccount;
    }

    @Transactional
    public void delete(UUID accountId) {
        logger.info("Starting account deletion process for accountId: {}", accountId);

        AccountDomain account = getAccountOrThrow(accountId);
        deleteAssociatedTransactions(account);

        accountAdapter.delete(accountId);
        logger.info("Account with ID: {} has been deleted successfully.", accountId);
    }

    private AccountDomain getAccountOrThrow(UUID accountId) {
        return Optional.ofNullable(accountAdapter.getById(accountId))
                .orElseThrow(() -> new DataBaseException(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), String.format(ACCOUNT_NOT_FOUND, accountId)));
    }

    private boolean accountExists(String accountNumber) {
        return accountAdapter.findByAccountNumber(accountNumber) != null;
    }

    private void validateNonModifiableFields(AccountDomain accountDomain, AccountDomain existingAccount) {
        if (!existingAccount.getAccountNumber().equals(accountDomain.getAccountNumber()) ||
                !existingAccount.getClientId().equals(accountDomain.getClientId())) {
            logAndThrowError("Attempted to modify non-modifiable fields (accountNumber, accountType, or clientId) for accountId: {}",
                    accountDomain.getId(), SPError.UNCHANGEABLE_ACCOUNT_DATA, "Cannot modify accountNumber, accountType, or clientId");
        }
    }

    private void deleteAssociatedTransactions(AccountDomain account) {
        var transactions = transactionAdapter.getTransactionsByAccount(account);
        if (!transactions.isEmpty()) {
            logger.info("Found {} transactions for accountId: {}. Proceeding to delete transactions first.", transactions.size(), account.getId());
            transactions.forEach(transaction -> transactionAdapter.delete(transaction.getId()));
            logger.info("All transactions for accountId: {} have been deleted.", account.getId());
        }
    }

    private void logAndThrowError(String logMessage, Object... logArgs) {
        logger.error(logMessage, logArgs);
        throw new CustomException(SPError.UNCHANGEABLE_ACCOUNT_DATA.getErrorCode(), (String) logArgs[logArgs.length - 1]);
    }

    private void logAndThrowError(String logMessage, Object[] logArgs, SPError error, String errorMessage) {
        logger.error(logMessage, logArgs);
        throw new DataBaseException(error.getErrorCode(), errorMessage);
    }
}
