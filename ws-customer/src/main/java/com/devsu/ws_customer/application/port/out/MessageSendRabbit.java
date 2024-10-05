package com.devsu.ws_customer.application.port.out;

import com.devsu.ws_customer.domain.ClientDomain;

public interface MessageSendRabbit {

    void sendClientInfo(ClientDomain clientDomain);
}
