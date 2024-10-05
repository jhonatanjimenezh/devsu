package com.devsu.ws_account.domain;

import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionDomain {

    private UUID id;
    private Date date;
    private TransactionTypeDomain transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private AccountDomain account;
}
