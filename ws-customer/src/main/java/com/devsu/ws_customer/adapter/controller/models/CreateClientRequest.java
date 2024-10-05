package com.devsu.ws_customer.adapter.controller.models;

import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Gender cannot be null")
    private Integer gender;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private int age;

    @NotBlank(message = "Identification cannot be blank")
    @Size(min = 8, max = 20, message = "Identification must be between 8 and 20 characters")
    private String identification;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 digits")
    private String phoneNumber;

    @NotBlank(message = "Client ID cannot be blank")
    @Size(min = 5, max = 20, message = "Client ID must be between 5 and 20 characters")
    private String clientId;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

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
                .person(personDomain)
                .clientId(this.clientId)
                .password(this.password)
                .status(true)
                .build();
    }
}
