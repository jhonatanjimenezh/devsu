package com.devsu.ws_account.application.port.in.account;

import com.devsu.ws_account.domain.AccountDomain;

import java.util.List;

public interface GetAllAccountsPort {
    List<AccountDomain> getAll();
}
