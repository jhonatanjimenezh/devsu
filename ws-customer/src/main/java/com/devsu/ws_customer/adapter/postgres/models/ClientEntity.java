package com.devsu.ws_customer.adapter.postgres.models;

import com.devsu.ws_customer.domain.ClientDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.util.UUID;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    public ClientDomain toDomain() {
        return ClientDomain.builder()
                .id(this.id)
                .clientId(this.clientId)
                .password(this.password)
                .status(this.status)
                .person(this.person.toDomain())
                .build();
    }

    public static ClientEntity fromDomain(ClientDomain domain) {
        return ClientEntity.builder()
                .id(domain.getId())
                .clientId(domain.getClientId())
                .password(domain.getPassword())
                .status(domain.isStatus())
                .person(PersonEntity.fromDomain(domain.getPerson()))
                .build();
    }
}
