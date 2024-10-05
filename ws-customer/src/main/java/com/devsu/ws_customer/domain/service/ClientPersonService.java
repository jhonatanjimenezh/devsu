package com.devsu.ws_customer.domain.service;

import com.devsu.ws_customer.adapter.postgres.ClientPostgresAdapter;
import com.devsu.ws_customer.adapter.postgres.PersonPostgresAdapter;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

public class ClientPersonService {

    private static final Logger logger = LoggerFactory.getLogger(ClientPersonService.class);
    private static final String PERSON_NOT_FOUND = "Person with ID %s not found";
    private static final String CLIENT_NOT_FOUND = "Client with ID %s not found";

    private final ClientPostgresAdapter clientAdapter;
    private final PersonPostgresAdapter personAdapter;



    public ClientPersonService(ClientPostgresAdapter clientAdapter, PersonPostgresAdapter personAdapter) {
        this.clientAdapter = clientAdapter;
        this.personAdapter = personAdapter;
    }

    @Transactional
    public ClientDomain create(ClientDomain clientDomain) {
        logger.info("Starting client creation process for clientId: {}", clientDomain.getClientId());

        if (personExists(clientDomain.getPerson().getIdentification())) {
            logAndThrowError("Person with identification {} already exists",
                    clientDomain.getPerson().getIdentification(), SPError.CUSTOMER_ADAPTER_SAVE_ERROR, "Person already exists");
        }

        PersonDomain createdPerson = personAdapter.save(clientDomain.getPerson());
        logger.info("Person created successfully with ID: {}", createdPerson.getId());

        clientDomain.setPerson(createdPerson);
        ClientDomain createdClient = clientAdapter.save(clientDomain);
        logger.info("Client created successfully with ID: {}", createdClient.getId());

        return createdClient;
    }

    @Transactional
    public ClientDomain update(ClientDomain clientDomain) {
        logger.info("Starting client update process for clientId: {}", clientDomain.getClientId());

        ClientDomain existingClient = getClientOrThrow(clientDomain.getId());
        PersonDomain existingPerson = getPersonOrThrow(existingClient.getPerson().getId());

        clientDomain.getPerson().setId(existingPerson.getId());
        PersonDomain updatedPerson = personAdapter.update(clientDomain.getPerson());
        logger.info("Person updated successfully with ID: {}", updatedPerson.getId());

        clientDomain.setPerson(updatedPerson);
        ClientDomain updatedClient = clientAdapter.update(clientDomain);
        logger.info("Client updated successfully with ID: {}", updatedClient.getId());

        return updatedClient;
    }

    @Transactional
    public void delete(UUID clientId) {
        logger.info("Starting client deletion process for clientId: {}", clientId);

        ClientDomain existingClient = getClientOrThrow(clientId);
        PersonDomain existingPerson = getPersonOrThrow(existingClient.getPerson().getId());

        clientAdapter.delete(clientId);
        logger.info("Client deleted successfully with ID: {}", clientId);

        personAdapter.delete(existingPerson.getId());
        logger.info("Person deleted successfully with ID: {}", existingPerson.getId());
    }

    private ClientDomain getClientOrThrow(UUID clientId) {
        return Optional.ofNullable(clientAdapter.getById(clientId))
                .orElseThrow(() -> new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(),
                        String.format(CLIENT_NOT_FOUND, clientId)));
    }

    private PersonDomain getPersonOrThrow(UUID personId) {
        return Optional.ofNullable(personAdapter.getById(personId))
                .orElseThrow(() -> new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(),
                        String.format(PERSON_NOT_FOUND, personId)));
    }

    private boolean personExists(String identification) {
        return personAdapter.findByIdentification(identification) != null;
    }

    private void logAndThrowError(String logMessage, Object logArg, SPError error, String errorMessage) {
        logger.error(logMessage, logArg);
        throw new DataBaseException(error.getErrorCode(), errorMessage);
    }
}
