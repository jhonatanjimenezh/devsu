package com.devsu.ws_customer.adapter.controller.models;

import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateRequest {

    @NotNull(message = "Client ID is mandatory")
    private UUID id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Gender cannot be null")
    private Integer gender;

    @Min(value = 1, message = "Age must be greater than 0")
    private int age;

    @NotBlank(message = "Identification is mandatory")
    @Size(max = 20, message = "Identification must not exceed 20 characters")
    private String identification;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Client ID is mandatory")
    @Size(max = 20, message = "Client ID must not exceed 20 characters")
    private String clientId;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Status is mandatory")
    private Boolean status;

    public ClientDomain toDomain() {
        GenderDomain genderDomain = GenderDomain.builder()
                .id(this.gender)
                .build();

        PersonDomain personDomain = PersonDomain.builder()
                .identification(this.identification)
                .name(this.name)
                .gender(genderDomain)
                .age(this.age)
                .address(this.address)
                .phone(this.phoneNumber)
                .build();

        return ClientDomain.builder()
                .id(this.id)
                .person(personDomain)
                .clientId(this.clientId)
                .password(this.password)
                .status(this.status)
                .build();
    }
}
