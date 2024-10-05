package com.devsu.ws_account.application.port.in.transaction;

import com.devsu.ws_account.domain.TransactionDomain;

import java.util.UUID;

public interface GetTransactionByIdPort {
    TransactionDomain getById(UUID id);
}
