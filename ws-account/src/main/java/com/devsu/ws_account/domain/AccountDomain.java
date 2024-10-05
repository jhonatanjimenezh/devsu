package com.devsu.ws_account.domain;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountDomain {

    private UUID id;
    private String accountNumber;
    private AccountTypeDomain accountType;
    private BigDecimal initialBalance;
    private Boolean status;
    private UUID clientId;
}
