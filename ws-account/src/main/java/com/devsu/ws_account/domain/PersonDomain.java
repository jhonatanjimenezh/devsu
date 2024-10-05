package com.devsu.ws_account.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PersonDomain {

    private UUID id;
    private String name;
    private GenderDomain gender;
    private int age;
    private String identification;
    private String address;
    private String phone;
}
