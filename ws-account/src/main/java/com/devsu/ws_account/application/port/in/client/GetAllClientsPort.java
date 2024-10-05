package com.devsu.ws_account.application.port.in.client;

import com.devsu.ws_account.domain.ClientDomain;

import java.util.List;

public interface GetAllClientsPort {
    List<ClientDomain> getAll();
}
