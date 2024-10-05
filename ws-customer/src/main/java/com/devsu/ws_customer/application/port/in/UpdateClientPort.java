package com.devsu.ws_customer.application.port.in;

import com.devsu.ws_customer.domain.ClientDomain;

public interface UpdateClientPort {
    ClientDomain update(ClientDomain domain);
}
