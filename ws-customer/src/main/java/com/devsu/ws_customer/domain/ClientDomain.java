package com.devsu.ws_customer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDomain {

    private UUID id;
    private String clientId;
    private String password;
    private boolean status;
    private PersonDomain person;
}
