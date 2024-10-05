package com.devsu.ws_customer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDomain {

    private UUID id;
    private String name;
    private GenderDomain gender;
    private int age;
    private String identification;
    private String address;
    private String phone;
}
