package com.devsu.ws_customer.adapter.rabbit.model;

import com.devsu.ws_customer.domain.ClientDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO implements Serializable {

    private UUID id;
    private String clientId;
    private String password;
    private boolean status;
    private PersonDTO person;

    public static ClientDTO fromDomain(ClientDomain clientDomain) {
        return ClientDTO.builder()
                .id(clientDomain.getId())
                .clientId(clientDomain.getClientId())
                .password(clientDomain.getPassword())
                .status(clientDomain.isStatus())
                .person(PersonDTO.fromDomain(clientDomain.getPerson()))
                .build();
    }

    public ClientDomain toDomain() {
        return ClientDomain.builder()
                .id(this.id)
                .clientId(this.clientId)
                .password(this.password)
                .status(this.status)
                .person(this.person.toDomain())
                .build();
    }
}
