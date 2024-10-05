package com.devsu.ws_customer.adapter.controller;

import com.devsu.ws_customer.adapter.controller.models.ClientUpdateRequest;
import com.devsu.ws_customer.adapter.controller.models.CreateClientRequest;
import com.devsu.ws_customer.application.port.in.*;
import com.devsu.ws_customer.config.exception.CustomException;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.ErrorResponse;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.adapter.controller.models.ClientResponse;
import com.devsu.ws_customer.domain.ClientDomain;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {})
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final CreateClientPort createClientPort;
    private final GetAllClientsPort getAllClientsPort;
    private final GetClientByIdPort getClientByIdPort;
    private final UpdateClientPort updateClientPort;
    private final DeleteClientPort deleteClientPort;
    private final GetAllClientsPaginatedPort getAllClientsPaginatedPort;

    public ClientController(CreateClientPort createClientPort, GetAllClientsPort getAllClientsPort,
                            GetClientByIdPort getClientByIdPort, UpdateClientPort updateClientPort,
                            DeleteClientPort deleteClientPort, GetAllClientsPaginatedPort getAllClientsPaginatedPort) {
        this.createClientPort = createClientPort;
        this.getAllClientsPort = getAllClientsPort;
        this.getClientByIdPort = getClientByIdPort;
        this.updateClientPort = updateClientPort;
        this.deleteClientPort = deleteClientPort;
        this.getAllClientsPaginatedPort = getAllClientsPaginatedPort;
    }

    @PostMapping
    public ResponseEntity<Object> createClient(@Valid @RequestBody CreateClientRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while creating client: {}", bindingResult.getFieldErrors());
            return handleValidationErrors(bindingResult);
        }

        try {
            logger.info("Creating client: {}", request);
            ClientDomain createdClient = createClientPort.create(request.toDomain());
            logger.info("Client created successfully with ID: {}", createdClient.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.of(createdClient, HttpStatus.CREATED));
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_CREATE_ERROR, ex);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllClients() {
        try {
            logger.info("Fetching all clients");
            List<ClientDomain> clients = getAllClientsPort.getAll();
            return ResponseEntity.ok(ClientResponse.of(clients, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR, ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getClientById(@PathVariable UUID id) {
        try {
            logger.info("Fetching client by ID: {}", id);
            ClientDomain client = getClientByIdPort.getById(id);
            logger.info("Successfully retrieved client with ID: {}", client.getId());
            return ResponseEntity.ok(ClientResponse.of(client, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR, ex);
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateClient(@Valid @RequestBody ClientUpdateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while updating client: {}", bindingResult.getFieldErrors());
            return handleValidationErrors(bindingResult);
        }

        try {
            logger.info("Updating client: {}", request);
            ClientDomain updatedClient = updateClientPort.update(request.toDomain());
            logger.info("Client updated successfully with ID: {}", updatedClient.getId());
            return ResponseEntity.ok(ClientResponse.of(updatedClient, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR, ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClient(@PathVariable UUID id) {
        try {
            logger.info("Deleting client with ID: {}", id);
            deleteClientPort.delete(id);
            logger.info("Client with ID: {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_DELETE_ERROR, ex);
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Object> getAllClientsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Fetching paginated clients - page: {}, size: {}", page, size);
            Page<ClientDomain> clientsPage = getAllClientsPaginatedPort.getAllPaginated(page, size);
            return ResponseEntity.ok(ClientResponse.of(clientsPage.getContent(), HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR, ex);
        }
    }

    private ResponseEntity<Object> handleValidationErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(ClientResponse.badRequest(bindingResult));
    }

    private ResponseEntity<Object> handleError(SPError error, Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(false, error.getErrorCode(), error.getErrorMessage(), ex.getCause());
        logger.error("Error occurred: {}", errorResponse, ex);

        HttpStatus status = (ex instanceof DataBaseException || ex instanceof CustomException)
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(errorResponse);
    }
}
