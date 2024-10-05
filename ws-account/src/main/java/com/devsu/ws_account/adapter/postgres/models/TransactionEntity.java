package com.devsu.ws_account.adapter.postgres.models;

import com.devsu.ws_account.domain.TransactionDomain;

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
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id", nullable = false, updatable = false)
    private TransactionTypeEntity transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private AccountEntity account;

    public TransactionDomain toDomain() {
        return TransactionDomain.builder()
                .id(this.id)
                .date(this.date)
                .transactionType(this.transactionType.toDomain())
                .amount(this.amount)
                .balance(this.balance)
                .account(this.account.toDomain())
                .build();
    }

    public static TransactionEntity fromDomain(TransactionDomain domain) {
        return TransactionEntity.builder()
                .id(domain.getId())
                .date(domain.getDate())
                .transactionType(TransactionTypeEntity.fromDomain(domain.getTransactionType()))
                .amount(domain.getAmount())
                .balance(domain.getBalance())
                .account(AccountEntity.fromDomain(domain.getAccount()))
                .build();
    }


}
