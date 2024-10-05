package com.devsu.ws_account.application.port.in.transaction;

import java.util.UUID;

public interface DeleteTransactionPort {
    void delete(UUID id);
}
