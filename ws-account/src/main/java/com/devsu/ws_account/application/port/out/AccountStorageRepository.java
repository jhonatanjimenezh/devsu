package com.devsu.ws_account.application.port.out;


import com.devsu.ws_account.domain.AccountDomain;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AccountStorageRepository {

    AccountDomain save(AccountDomain domain);
    List<AccountDomain> getAll();

    AccountDomain getById(UUID id);
    Page<AccountDomain> getAllPaginated(int page, int size);
    AccountDomain update(AccountDomain domain);
    void delete(UUID id);
    public AccountDomain findByAccountNumber(String accountNumber);
    public AccountDomain findByClientId(UUID clientId);
}
