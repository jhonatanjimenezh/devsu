package com.devsu.ws_account.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBaseException extends GenericException {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseException.class);

    public DataBaseException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        logger.error("Exception DataBaseException: code:{}, message:{}, cause:{}", errorCode, message, cause.getCause());
    }

    public DataBaseException(int errorCode, String message) {
        super(errorCode, message);
        logger.error("Exception DataBaseException: code:{}, message:{}", errorCode, message);
    }
}
