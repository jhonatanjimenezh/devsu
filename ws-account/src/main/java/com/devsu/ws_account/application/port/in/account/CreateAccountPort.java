package com.devsu.ws_account.application.port.in.account;

import com.devsu.ws_account.domain.AccountDomain;

public interface CreateAccountPort {
    AccountDomain create(AccountDomain domain);
}
