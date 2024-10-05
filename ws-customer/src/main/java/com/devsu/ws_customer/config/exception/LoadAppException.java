package com.devsu.ws_customer.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadAppException extends GenericException {

    private static final Logger logger = LoggerFactory.getLogger(LoadAppException.class);

    public LoadAppException(int errorCode, String message) {
        super(errorCode, message);
        logger.error("Exception LoadAppException: code:{}, message:{}", errorCode, message);
    }

    public LoadAppException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        logger.error("Exception LoadAppException: code:{}, message:{}", errorCode, message);
    }
}
