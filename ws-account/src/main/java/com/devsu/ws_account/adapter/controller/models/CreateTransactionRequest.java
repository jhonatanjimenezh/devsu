package com.devsu.ws_account.adapter.controller.models;

import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.TransactionTypeDomain;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "Transaction type cannot be null")
    @Min(value = 1, message = "Transaction type must be greater than 0")
    private Integer transactionType;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Account ID cannot be null")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Account ID must be a valid UUID")
    private String accountId;

    public TransactionDomain toDomain() {
        TransactionTypeDomain transactionTypeDomain = TransactionTypeDomain.builder()
                .id(this.transactionType)
                .build();

        AccountDomain accountDomain = AccountDomain.builder()
                .id(UUID.fromString(this.accountId))
                .build();

        return TransactionDomain.builder()
                .transactionType(transactionTypeDomain)
                .amount(this.amount)
                .account(accountDomain)
                .build();
    }
}
