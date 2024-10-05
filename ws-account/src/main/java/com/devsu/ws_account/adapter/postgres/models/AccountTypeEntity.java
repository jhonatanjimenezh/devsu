package com.devsu.ws_account.adapter.postgres.models;

import com.devsu.ws_account.domain.AccountTypeDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounttype")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public AccountTypeDomain toDomain(){
        return AccountTypeDomain.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

    public static AccountTypeEntity fromDomain(AccountTypeDomain domain){
        return AccountTypeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
