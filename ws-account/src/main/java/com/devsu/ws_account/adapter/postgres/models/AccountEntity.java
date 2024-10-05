package com.devsu.ws_account.adapter.postgres.models;

import com.devsu.ws_account.domain.AccountDomain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountTypeEntity accountType;

    @Column(nullable = false)
    private BigDecimal initialBalance;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false, updatable = false)
    private UUID clientId;

    public AccountDomain toDomain() {
        return AccountDomain.builder()
                .id(this.id)
                .accountNumber(this.accountNumber)
                .accountType(this.accountType.toDomain())
                .initialBalance(this.initialBalance)
                .status(this.status)
                .clientId(this.clientId)
                .build();
    }

    public static AccountEntity fromDomain(AccountDomain domain) {
        return AccountEntity.builder()
                .id(domain.getId())
                .accountNumber(domain.getAccountNumber())
                .accountType(AccountTypeEntity.fromDomain(domain.getAccountType()))
                .initialBalance(domain.getInitialBalance())
                .status(domain.getStatus())
                .clientId(domain.getClientId())
                .build();
    }


}
