package com.devsu.ws_account.config.exception;

public enum SPError {

    GENERIC_ERROR(1000, "Unexpected error occurred"),
    APP_LOAD_ERROR(1001, "Application failed to start properly"),
    DATABASE_CONNECTION_ERROR(1002, "Database connection failed"),
    INVALID_ARGUMENT_ERROR(1003, "Invalid argument provided"),
    INVALID_REQUEST_ERROR(1004, "Malformed request: invalid or unreadable message body"),
    INVALID_PARAMS_ERROR(1005, "Invalid parameters provided in the request"),

    ACCOUNT_ADAPTER_SAVE_ERROR(1006, "Error while saving account data in the database"),
    ACCOUNT_ADAPTER_FIND_ERROR(1007, "Error while retrieving account data from the database"),
    ACCOUNT_ADAPTER_UPDATE_ERROR(1008, "Error while updating account data in the database"),
    ACCOUNT_ADAPTER_DELETE_ERROR(1009, "Error while deleting account data from the database"),

    ACCOUNT_CONTROLLER_CREATE_ERROR(1010, "Failed to create new account"),
    ACCOUNT_CONTROLLER_FIND_ALL_ERROR(1011, "Failed to retrieve all accounts"),
    ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR(1012, "Failed to retrieve account by ID"),
    ACCOUNT_CONTROLLER_UPDATE_ERROR(1013, "Failed to update account information"),
    ACCOUNT_CONTROLLER_DELETE_ERROR(1014, "Failed to delete account"),
    UNCHANGEABLE_ACCOUNT_DATA(1015, "Unchangeable account data"),
    BALANCE_NOT_AVAILABLE_FOR_TRANSACTION(1016, "Balance not available for transaction"),
    INVALID_TRANSACTION_UPDATE_ERROR(1017, "Failed to update transaction information"),
    RABBITMQ_RECEIVE_ERROR(1018, "Error receiving message from RabbitMQ");



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
