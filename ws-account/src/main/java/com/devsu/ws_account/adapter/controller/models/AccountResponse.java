package com.devsu.ws_account.adapter.controller.models;

import com.devsu.ws_account.config.exception.SPError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;


public class AccountResponse extends GenericResponse {


    public static AccountResponse of(Object result, HttpStatus httpStatus) {
        AccountResponse response = new AccountResponse();
        response.setStatus(true);
        response.setCodeStatus(httpStatus.value());
        response.setMessage(httpStatus.getReasonPhrase());
        response.setData(result);
        return response;
    }

    public static AccountResponse badRequest(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> String.format("Field: %s -> Error: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        AccountResponse response = new AccountResponse();
        response.setStatus(false);
        response.setCodeStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(SPError.INVALID_PARAMS_ERROR.getErrorMessage());
        response.setData(errors);

        return response;
    }
}

