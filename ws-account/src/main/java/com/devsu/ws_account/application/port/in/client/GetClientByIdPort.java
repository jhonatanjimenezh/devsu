package com.devsu.ws_account.application.port.in.client;

import com.devsu.ws_account.domain.ClientDomain;

public interface GetClientByIdPort {
    ClientDomain getById(Long id);
}
