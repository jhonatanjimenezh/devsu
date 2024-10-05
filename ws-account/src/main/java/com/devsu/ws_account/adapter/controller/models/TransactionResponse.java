package com.devsu.ws_account.adapter.controller.models;

import com.devsu.ws_account.config.exception.SPError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionResponse extends GenericResponse {

    public static TransactionResponse of(Object result, HttpStatus httpStatus) {
        TransactionResponse response = new TransactionResponse();
        response.setStatus(true);
        response.setCodeStatus(httpStatus.value());
        response.setMessage(httpStatus.getReasonPhrase());
        response.setData(result);
        return response;
    }

    public static TransactionResponse badRequest(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> String.format("Field: %s -> Error: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        TransactionResponse response = new TransactionResponse();
        response.setStatus(false);
        response.setCodeStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(SPError.INVALID_PARAMS_ERROR.getErrorMessage());
        response.setData(errors);

        return response;
    }
}
