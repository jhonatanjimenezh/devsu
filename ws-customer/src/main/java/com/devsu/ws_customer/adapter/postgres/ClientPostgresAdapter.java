package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.ClientEntity;
import com.devsu.ws_customer.application.port.out.ClientStorageRepository;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClientPostgresAdapter implements ClientStorageRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClientPostgresAdapter.class);
    private final ClientPostgresRepository repository;

    @Autowired
    public ClientPostgresAdapter(ClientPostgresRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ClientDomain save(ClientDomain domain) {
        try {
            logger.info("Attempting to save client: {}", domain.toString());
            ClientEntity entity = ClientEntity.fromDomain(domain);
            ClientEntity savedEntity = repository.save(entity);
            logger.info("Client saved successfully: {}", savedEntity.toDomain());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error saving client: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDomain> getAll() {
        try {
            logger.info("Retrieving all clients from the database");
            return repository.findAll().stream()
                    .map(ClientEntity::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving clients: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDomain getById(UUID id) {
        try {
            logger.info("Retrieving client by ID: {}", id);
            return repository.findById(id)
                    .map(ClientEntity::toDomain)
                    .orElseThrow(() -> new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), "Client not found"));
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving client by ID: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDomain> getAllPaginated(int page, int size) {
        try {
            logger.info("Retrieving clients paginated");
            Pageable pageable = PageRequest.of(page, size);
            return repository.findAll(pageable)
                    .map(ClientEntity::toDomain);
        } catch (Exception e) {
            logger.error("Error retrieving clients paginated: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public ClientDomain update(ClientDomain domain) {
        try {
            logger.info("Attempting to update client: {}", domain);
            if (!repository.existsById(domain.getId())) {
                throw new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Client not found");
            }
            ClientEntity entity = ClientEntity.fromDomain(domain);
            ClientEntity updatedEntity = repository.save(entity);
            logger.info("Client updated successfully: {}", updatedEntity.toDomain());
            return updatedEntity.toDomain();
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating client: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            logger.info("Attempting to delete client by ID: {}", id);
            repository.deleteById(id);
            logger.info("Client deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting client: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDomain findByClientId(String clientId) {
        try {
            logger.info("Retrieving client by clientId: {}", clientId);
            return repository.findByClientId(clientId)
                    .map(ClientEntity::toDomain)
                    .orElseThrow(() -> new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), "Client not found"));
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving client by ID: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }
}
