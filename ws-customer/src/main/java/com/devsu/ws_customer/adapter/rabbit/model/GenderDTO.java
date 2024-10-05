package com.devsu.ws_customer.adapter.rabbit.model;

import com.devsu.ws_customer.domain.GenderDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenderDTO implements Serializable {

    private Integer id;
    private String genderName;

    public static GenderDTO fromDomain(GenderDomain genderDomain) {
        return GenderDTO.builder()
                .id(genderDomain.getId())
                .genderName(genderDomain.getGenderName())
                .build();
    }

    public GenderDomain toDomain() {
        return GenderDomain.builder()
                .id(this.id)
                .genderName(this.genderName)
                .build();
    }
}
