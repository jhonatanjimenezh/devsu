package com.devsu.ws_account.adapter.controller.models;

import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {

    @NotNull(message = "Account ID cannot be null")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Account ID must be a valid UUID")
    private String id;

    @NotBlank(message = "Account number cannot be blank")
    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters")
    private String accountNumber;

    @NotNull(message = "Account type cannot be null")
    @Min(value = 1, message = "Account type must be greater than 0")
    private Integer accountType;

    @DecimalMin(value = "0.00", inclusive = true, message = "Initial balance must be greater than or equal to 0")
    private BigDecimal initialBalance;

    @NotNull(message = "Status cannot be null")
    @AssertTrue(message = "Status must be either true or false")
    private Boolean status;

    @NotNull(message = "Client ID cannot be null")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Client ID must be a valid UUID")
    private String clientId;

    public AccountDomain toDomain() {
        AccountTypeDomain accountTypeDomain = AccountTypeDomain.builder()
                .id(accountType)
                .build();

        return AccountDomain.builder()
                .id(UUID.fromString(this.id))
                .accountNumber(this.accountNumber)
                .accountType(accountTypeDomain)
                .initialBalance(this.initialBalance)
                .status(this.status)
                .clientId(UUID.fromString(this.clientId))
                .build();
    }
}
