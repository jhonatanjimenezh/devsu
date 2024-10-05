package com.devsu.ws_account.application.port.in.account;

import java.util.UUID;

public interface DeleteAccountPort {
    void delete(UUID id);
}

