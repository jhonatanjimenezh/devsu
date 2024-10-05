package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.UpdateClientPort;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.service.ClientPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateClientUseCase implements UpdateClientPort {

    private static final Logger logger = LoggerFactory.getLogger(UpdateClientUseCase.class);
    private final ClientPersonService service;

    public UpdateClientUseCase(ClientPersonService service) {
        this.service = service;
    }

    @Override
    public ClientDomain update(ClientDomain domain) {
        try {
            logger.info("Attempting to update client with ID: {}", domain.getClientId());
            return service.update(domain);
        } catch (DataBaseException d) {
            logger.error("Database error while updating client: {}", domain.getClientId(), d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR.getErrorCode(),
                    "Failed to update client: " + domain.getClientId(), d);
        } catch (Exception e) {
            logger.error("Unexpected error while updating client: {}", domain.getClientId(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client update", e);
        }
    }
}
