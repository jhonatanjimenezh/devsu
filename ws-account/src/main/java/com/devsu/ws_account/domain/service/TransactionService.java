package com.devsu.ws_account.domain.service;

import com.devsu.ws_account.adapter.postgres.AccountPostgresAdapter;
import com.devsu.ws_account.adapter.postgres.TransactionPostgresAdapter;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionPostgresAdapter transactionAdapter;
    private final AccountPostgresAdapter accountAdapter;

    public TransactionService(TransactionPostgresAdapter transactionAdapter, AccountPostgresAdapter accountAdapter) {
        this.transactionAdapter = transactionAdapter;
        this.accountAdapter = accountAdapter;
    }

    @Transactional
    public TransactionDomain create(TransactionDomain transactionDomain) {
        logger.info("Starting transaction creation process for account ID: {}", transactionDomain.getAccount().getId());

        AccountDomain existingAccount = verifyAccountExists(transactionDomain.getAccount().getId());
        transactionDomain.setAccount(existingAccount);
        BigDecimal availableBalance = calculateAvailableBalance(transactionDomain.getAccount().getId());

        BigDecimal newBalance = updateBalance(availableBalance, transactionDomain);

        transactionDomain.setBalance(newBalance);
        transactionDomain.setDate(currentDate());
        transactionDomain.setAmount(this.castWithdrawal(transactionDomain));

        TransactionDomain createdTransaction = transactionAdapter.save(transactionDomain);
        logger.info("Transaction created successfully with ID: {} and new balance: {}", createdTransaction.getId(), createdTransaction.getBalance());

        return createdTransaction;
    }

    @Transactional
    public TransactionDomain update(TransactionDomain transactionDomain) {
        logger.info("Starting transaction update process for transaction ID: {}", transactionDomain.getId());

        AccountDomain existingAccount = verifyAccountExists(transactionDomain.getAccount().getId());
        transactionDomain.setAccount(existingAccount);
        TransactionDomain lastTransaction = verifyLastTransaction(transactionDomain);

        BigDecimal amount = this.castWithdrawal(lastTransaction);
        BigDecimal availableBalance = lastTransaction.getBalance().subtract(amount);
        BigDecimal newBalance = updateBalance(availableBalance, transactionDomain);

        lastTransaction.setBalance(newBalance);
        lastTransaction.setDate(currentDate());
        lastTransaction.setAmount(this.castWithdrawal(transactionDomain));

        TransactionDomain updatedTransaction = transactionAdapter.update(lastTransaction);
        logger.info("Transaction updated successfully with ID: {} and new balance: {}", updatedTransaction.getId(), updatedTransaction.getBalance());

        return updatedTransaction;
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Starting transaction delete process for transaction ID: {}", id);

        TransactionDomain transactionDomain = transactionAdapter.getById(id);
        if (transactionDomain == null) {
            logger.error("No transaction found for transaction ID: {}", id);
            throw new CustomException(SPError.INVALID_TRANSACTION_UPDATE_ERROR.getErrorCode(), "No transaction found.");
        }

        TransactionDomain lastTransaction = verifyLastTransaction(transactionDomain);
        transactionAdapter.delete(lastTransaction.getId());
        logger.info("Transaction deleted successfully with ID: {}", lastTransaction.getId());
    }

    private AccountDomain verifyAccountExists(UUID accountId) {
        AccountDomain existingAccount = accountAdapter.getById(accountId);
        if (existingAccount == null) {
            logger.error("Account with ID {} does not exist", accountId);
            throw new DataBaseException(SPError.ACCOUNT_ADAPTER_SAVE_ERROR.getErrorCode(), "Account does not exist");
        }
        return existingAccount;
    }

    private TransactionDomain verifyLastTransaction(TransactionDomain transactionDomain) {
        TransactionDomain lastTransaction = transactionAdapter.getLastTransactionByAccount(transactionDomain.getAccount());
        if (lastTransaction == null || !lastTransaction.getId().equals(transactionDomain.getId())) {
            logger.error("Transaction ID {} cannot be modified/deleted as it is not the last transaction for account ID: {}",
                    transactionDomain.getId(), transactionDomain.getAccount().getId());
            throw new CustomException(SPError.INVALID_TRANSACTION_UPDATE_ERROR.getErrorCode(),
                    "This transaction cannot be modified/deleted as it is not the most recent one.");
        }
        return lastTransaction;
    }

    private BigDecimal calculateAvailableBalance(UUID accountId) {
        TransactionDomain lastTransaction = transactionAdapter.getLastTransactionByAccount(accountAdapter.getById(accountId));
        if (lastTransaction == null) {
            AccountDomain account = accountAdapter.getById(accountId);
            return account.getInitialBalance();
        }
        return lastTransaction.getBalance();
    }

    private BigDecimal updateBalance(BigDecimal availableBalance, TransactionDomain domain) {
        BigDecimal amount = this.castWithdrawal(domain);

        BigDecimal newBalance = availableBalance.add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Insufficient funds. Available balance: {}, transaction amount: {}", availableBalance, amount);
            throw new CustomException(SPError.BALANCE_NOT_AVAILABLE_FOR_TRANSACTION.getErrorCode(),
                    "Insufficient funds for this transaction");
        }

        return newBalance;
    }

    private BigDecimal castWithdrawal(TransactionDomain domain){
        BigDecimal amount = domain.getAmount();
        if (domain.getTransactionType().getId().equals(2)) {
            amount = amount.negate();
        }
        return amount;
    }


    private Date currentDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}
