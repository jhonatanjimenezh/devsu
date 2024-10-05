package com.devsu.ws_account.adapter.controller.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericResponse {

    @Schema(description = "Indicates the success or failure of the operation.")
    private boolean status;

    @Schema(description = "The HTTP status code of the response.")
    private int codeStatus;

    @Schema(description = "A message providing more details about the response.")
    private String message;

    @Schema(description = "The data payload of the response, which can be any type of object.")
    private Object data;
}
