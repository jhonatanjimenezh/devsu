package com.devsu.ws_account.application.port.in.transaction;

import com.devsu.ws_account.domain.TransactionDomain;

public interface CreateTransactionPort {
    TransactionDomain create(TransactionDomain domain);
}

