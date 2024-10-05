package com.devsu.ws_account.application.port.in.account;

import com.devsu.ws_account.domain.AccountDomain;

import java.util.UUID;

public interface GetAccountByIdPort {
    AccountDomain getById(UUID id);
}
