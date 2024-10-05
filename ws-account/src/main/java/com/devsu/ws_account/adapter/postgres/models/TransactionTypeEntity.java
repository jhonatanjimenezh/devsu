package com.devsu.ws_account.adapter.postgres.models;

import com.devsu.ws_account.domain.TransactionTypeDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactiontype")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public TransactionTypeDomain toDomain(){
        return TransactionTypeDomain.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

    public static TransactionTypeEntity fromDomain(TransactionTypeDomain domain){
        return TransactionTypeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

}
