package com.devsu.ws_account.application.port.in.transaction;

import com.devsu.ws_account.domain.TransactionDomain;

import java.util.List;

public interface GetAllTransactionsPort {
    List<TransactionDomain> getAll();
}

