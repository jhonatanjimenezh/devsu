package com.devsu.ws_customer.adapter.controller.models;

import com.devsu.ws_customer.config.exception.SPError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ClientResponse extends GenericResponse {

    public static ClientResponse of(Object result, HttpStatus httpStatus) {
        ClientResponse response = new ClientResponse();
        response.setStatus(true);
        response.setCodeStatus(httpStatus.value());
        response.setMessage(httpStatus.getReasonPhrase());
        response.setData(result);
        return response;
    }

    public static ClientResponse badRequest(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> String.format("Field: %s -> Error: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ClientResponse response = new ClientResponse();
        response.setStatus(false);
        response.setCodeStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(SPError.INVALID_PARAMS_ERROR.getErrorMessage());
        response.setData(errors);

        return response;
    }
}
