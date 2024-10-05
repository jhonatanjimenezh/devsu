package com.devsu.ws_customer.application.usecase;

import com.devsu.ws_customer.application.port.in.CreateClientPort;
import com.devsu.ws_customer.application.port.out.MessageSendRabbit;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.service.ClientPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateClientUseCase implements CreateClientPort {

    private static final Logger logger = LoggerFactory.getLogger(CreateClientUseCase.class);
    private final ClientPersonService service;

    private final MessageSendRabbit message;

    public CreateClientUseCase(ClientPersonService service, MessageSendRabbit message) {
        this.service = service;
        this.message = message;
    }

    @Override
    public ClientDomain create(ClientDomain domain) {
        try {
            logger.info("Attempting to create client with ID: {}", domain.getClientId());
            ClientDomain response = service.create(domain);
            message.sendClientInfo(response);
            return response;
        } catch (DataBaseException d) {
            logger.error("Database error while creating client: {}", domain.getClientId(), d);
            throw new CustomException(SPError.CUSTOMER_CONTROLLER_CREATE_ERROR.getErrorCode(),
                    "Failed to create client: " + domain.getClientId(), d);
        } catch (Exception e) {
            logger.error("Unexpected error while creating client: {}", domain.getClientId(), e);
            throw new CustomException(SPError.GENERIC_ERROR.getErrorCode(),
                    "Unexpected error during client creation", e);
        }
    }
}
