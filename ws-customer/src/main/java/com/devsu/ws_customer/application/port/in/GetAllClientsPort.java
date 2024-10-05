package com.devsu.ws_customer.application.port.in;

import com.devsu.ws_customer.domain.ClientDomain;

import java.util.List;

public interface GetAllClientsPort {
    List<ClientDomain> getAll();
}
