package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.DeleteClientPort;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.service.ClientPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class DeleteClientUseCase implements DeleteClientPort {

    private static final Logger logger = LoggerFactory.getLogger(DeleteClientUseCase.class);
    private final ClientPersonService service;

    public DeleteClientUseCase(ClientPersonService service) {
        this.service = service;
    }

    @Override
    public void delete(UUID id) {
        try {
            logger.info("Attempting to delete client with ID: {}", id);
            service.delete(id);
        } catch (DataBaseException d) {
            logger.error("Database error while deleting client with ID: {}", id, d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_DELETE_ERROR.getErrorCode(),
                    "Failed to delete client with ID: " + id, d);
        } catch (Exception e) {
            logger.error("Unexpected error while deleting client with ID: {}", id, e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client deletion", e);
        }
    }
}
