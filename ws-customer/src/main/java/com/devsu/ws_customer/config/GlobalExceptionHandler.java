package com.devsu.ws_customer.config;


import com.devsu.ws_customer.config.exception.ErrorResponse;
import com.devsu.ws_customer.config.exception.GenericException;
import com.devsu.ws_customer.config.exception.SPError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(GenericException ex) {
        ErrorResponse error = new ErrorResponse(false, SPError.GENERIC_ERROR.getErrorCode(), SPError.GENERIC_ERROR.getErrorMessage(), ex.getMessage());
        logError(error, ex);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(false, SPError.INVALID_ARGUMENT_ERROR.getErrorCode(), SPError.INVALID_ARGUMENT_ERROR.getErrorMessage(), null);
        logError(error, ex);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> "Field: "+error.getField()+" -> Error:"+error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(false, SPError.INVALID_PARAMS_ERROR.getErrorCode(), SPError.INVALID_PARAMS_ERROR.getErrorMessage(), errors);
        logError(error, ex);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(false, SPError.INVALID_REQUEST_ERROR.getErrorCode(), SPError.INVALID_REQUEST_ERROR.getErrorMessage(), null);
        logError(error, ex);
        return ResponseEntity.badRequest().body(error);
    }

    private void logError(ErrorResponse error, Exception ex) {
        logger.error("Error:{} -> Exception: {}", error, ex);
    }
}
