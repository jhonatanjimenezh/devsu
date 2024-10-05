package com.devsu.ws_customer.adapter.postgres.models;

import com.devsu.ws_customer.domain.GenderDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "gender")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "gender_name", unique = true, nullable = false)
    private String genderName;

    public GenderDomain toDomain(){
        return GenderDomain.builder()
                .id(this.id)
                .genderName(this.genderName)
                .build();
    }

    public static GenderEntity fromDomain(GenderDomain domain){
        return GenderEntity.builder()
                .id(domain.getId())
                .genderName(domain.getGenderName())
                .build();
    }
}
