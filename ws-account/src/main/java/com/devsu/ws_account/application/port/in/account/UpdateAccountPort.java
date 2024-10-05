package com.devsu.ws_account.application.port.in.account;

import com.devsu.ws_account.domain.AccountDomain;

public interface UpdateAccountPort {
    AccountDomain update(AccountDomain domain);
}
