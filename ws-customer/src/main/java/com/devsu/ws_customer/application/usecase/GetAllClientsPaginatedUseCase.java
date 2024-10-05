package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.GetAllClientsPaginatedPort;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

public class GetAllClientsPaginatedUseCase implements GetAllClientsPaginatedPort {

    private static final Logger logger = LoggerFactory.getLogger(GetAllClientsPaginatedUseCase.class);
    private final ClientStorageRepository repository;

    public GetAllClientsPaginatedUseCase(ClientStorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ClientDomain> getAllPaginated(int page, int size) {
        try {
            logger.info("Attempting to retrieve clients in a paginated way");
            return repository.getAllPaginated(page, size);
        } catch (DataBaseException d) {
            logger.error("Database error while retrieving clients paginated", d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                    "Failed to retrieve clients paginated", d);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving clients paginated", e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client paginated retrieval", e);
        }
    }
}
