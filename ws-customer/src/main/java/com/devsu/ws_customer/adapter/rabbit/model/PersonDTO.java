package com.devsu.ws_customer.adapter.rabbit.model;

import com.devsu.ws_customer.domain.PersonDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO implements Serializable {

    private UUID id;
    private String name;
    private GenderDTO gender;
    private int age;
    private String identification;
    private String address;
    private String phone;

    public static PersonDTO fromDomain(PersonDomain personDomain) {
        return PersonDTO.builder()
                .id(personDomain.getId())
                .name(personDomain.getName())
                .gender(GenderDTO.fromDomain(personDomain.getGender()))
                .age(personDomain.getAge())
                .identification(personDomain.getIdentification())
                .address(personDomain.getAddress())
                .phone(personDomain.getPhone())
                .build();
    }

    public PersonDomain toDomain() {
        return PersonDomain.builder()
                .id(this.id)
                .name(this.name)
                .gender(this.gender.toDomain())
                .age(this.age)
                .identification(this.identification)
                .address(this.address)
                .phone(this.phone)
                .build();
    }
}
