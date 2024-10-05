package com.devsu.ws_customer.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueException extends GenericException {

    private static final Logger logger = LoggerFactory.getLogger(QueueException.class);

    public QueueException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        logger.error("Exception QueueException: code:{}, message:{}, cause:{}", errorCode, message, cause.getCause());
    }

    public QueueException(int errorCode, String message) {
        super(errorCode, message);
        logger.error("Exception QueueException: code:{}, message:{}", errorCode, message);
    }
}
