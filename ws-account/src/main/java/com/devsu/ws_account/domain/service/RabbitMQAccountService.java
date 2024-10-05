package com.devsu.ws_account.domain.service;

import com.devsu.ws_account.application.port.in.account.CreateAccountPort;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import com.devsu.ws_account.domain.ClientDomain;

import java.math.BigDecimal;

public class RabbitMQAccountService {

    private final CreateAccountPort createAccountPort;

    public RabbitMQAccountService(CreateAccountPort createAccountPort) {
        this.createAccountPort = createAccountPort;
    }

    public AccountDomain createAccountForClient(ClientDomain clientDomain) {
        AccountTypeDomain accountTypeDomain = AccountTypeDomain.builder()
                .id(1)
                .build();

        AccountDomain accountDomain = AccountDomain.builder()
                .accountNumber(clientDomain.getPerson().getPhone())
                .accountType(accountTypeDomain)
                .initialBalance(BigDecimal.ZERO)
                .clientId(clientDomain.getId())
                .status(true)
                .build();

        return createAccountPort.create(accountDomain);
    }
}
