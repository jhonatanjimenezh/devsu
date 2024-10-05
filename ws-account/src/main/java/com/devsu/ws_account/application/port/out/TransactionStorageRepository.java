package com.devsu.ws_account.application.port.out;


import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TransactionStorageRepository {

    TransactionDomain save(TransactionDomain domain);
    List<TransactionDomain> getAll();
    TransactionDomain getById(UUID id);
    Page<TransactionDomain> getAllPaginated(int page, int size);
    TransactionDomain update(TransactionDomain domain);
    void delete(UUID id);
    List<TransactionDomain> getTransactionsByAccount(AccountDomain account);
    TransactionDomain getLastTransactionByAccount(AccountDomain account);
}
