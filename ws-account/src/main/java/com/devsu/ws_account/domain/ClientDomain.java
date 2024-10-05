package com.devsu.ws_account.domain;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClientDomain {

    private UUID id;
    private String clientId;
    private String password;
    private boolean status;
    private PersonDomain person;
}
