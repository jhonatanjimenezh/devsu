package com.devsu.ws_customer.adapter.postgres.models;

import com.devsu.ws_customer.domain.PersonDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "gender_id", nullable = false)
    private GenderEntity gender;

    @Column(name = "age")
    private int age;

    @Column(name = "identification", nullable = false, unique = true, updatable = false)
    private String identification;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

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

    public static PersonEntity fromDomain(PersonDomain domain) {
        return PersonEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .gender(GenderEntity.fromDomain(domain.getGender()))
                .age(domain.getAge())
                .identification(domain.getIdentification())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .build();
    }
}
