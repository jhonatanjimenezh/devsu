package com.devsu.ws_account.config.exception;

public abstract class GenericException extends RuntimeException {

    private final int errorCode;

    public GenericException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GenericException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
