package com.devsu.ws_customer.application.port.in;

import com.devsu.ws_customer.domain.ClientDomain;

import java.util.UUID;

public interface GetClientByIdPort {
    ClientDomain getById(UUID id);
}
