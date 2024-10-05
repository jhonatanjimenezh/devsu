package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.GetClientByIdPort;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class GetClientByIdUseCase implements GetClientByIdPort {

    private static final Logger logger = LoggerFactory.getLogger(GetClientByIdUseCase.class);
    private final ClientStorageRepository repository;

    public GetClientByIdUseCase(ClientStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClientDomain getById(UUID id) {
        try {
            logger.info("Attempting to retrieve client by ID: {}", id);
            return repository.getById(id);
        } catch (DataBaseException d) {
            logger.error("Database error while retrieving client by ID: {}", id, d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode(),
                    "Failed to retrieve client by ID: " + id, d);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving client by ID: {}", id, e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client retrieval by ID", e);
        }
    }
}
