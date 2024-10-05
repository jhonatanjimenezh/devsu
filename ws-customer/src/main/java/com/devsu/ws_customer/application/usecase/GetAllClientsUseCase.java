package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.GetAllClientsPort;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class GetAllClientsUseCase implements GetAllClientsPort {

    private static final Logger logger = LoggerFactory.getLogger(GetAllClientsUseCase.class);
    private final ClientStorageRepository repository;

    public GetAllClientsUseCase(ClientStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClientDomain> getAll() {
        try {
            logger.info("Attempting to retrieve all clients");
            return repository.getAll();
        } catch (DataBaseException d) {
            logger.error("Database error while retrieving clients", d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                    "Failed to retrieve clients", d);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving clients", e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client retrieval", e);
        }
    }
}
