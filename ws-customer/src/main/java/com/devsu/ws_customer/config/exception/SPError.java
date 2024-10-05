package com.devsu.ws_customer.config.exception;

public enum SPError {

    GENERIC_ERROR(1000, "Unexpected error occurred"),
    APP_LOAD_ERROR(1001, "Application failed to start properly"),
    DATABASE_CONNECTION_ERROR(1002, "Database connection failed"),
    INVALID_ARGUMENT_ERROR(1003, "Invalid argument provided"),
    INVALID_REQUEST_ERROR(1004, "Malformed request: invalid or unreadable message body"),
    INVALID_PARAMS_ERROR(1005, "Invalid parameters provided in the request"),

    CUSTOMER_ADAPTER_SAVE_ERROR(1006, "Error while saving data in the database"),
    CUSTOMER_ADAPTER_FIND_ERROR(1007, "Error while retrieving data from the database"),
    CUSTOMER_ADAPTER_UPDATE_ERROR(1008, "Error while updating data in the database"),
    CUSTOMER_ADAPTER_DELETE_ERROR(1009, "Error while deleting data from the database"),

    CUSTOMER_CONTROLLER_CREATE_ERROR(1010, "Failed to create new client"),
    CUSTOMER_CONTROLLER_FIND_ALL_ERROR(1011, "Failed to retrieve all clients"),
    CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR(1012, "Failed to retrieve client by ID"),
    CUSTOMER_CONTROLLER_UPDATE_ERROR(1013, "Failed to update client information"),
    CUSTOMER_CONTROLLER_DELETE_ERROR(1014, "Failed to delete client"),
    RABBITMQ_SEND_ERROR(1015, "Failed send message rabbit");

    private final int errorCode;
    private final String errorMessage;

    SPError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
