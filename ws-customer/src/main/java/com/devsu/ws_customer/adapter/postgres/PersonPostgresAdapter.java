package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.PersonEntity;
import com.devsu.ws_customer.application.port.out.PersonStorageRepository;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.PersonDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Component
public class PersonPostgresAdapter implements PersonStorageRepository {

    private static final Logger logger = LoggerFactory.getLogger(PersonPostgresAdapter.class);
    private final PersonPostgresRepository repository;

    @Autowired
    public PersonPostgresAdapter(PersonPostgresRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public PersonDomain save(PersonDomain domain) {
        try {
            logger.info("Attempting to save person: {}", domain.toString());
            PersonEntity entity = PersonEntity.fromDomain(domain);
            PersonEntity savedEntity = repository.save(entity);
            logger.info("Person saved successfully: {}", savedEntity.toDomain());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Error saving person: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDomain getById(UUID id) {
        try {
            logger.info("Retrieving person by ID: {}", id);
            return repository.findById(id)
                    .map(PersonEntity::toDomain)
                    .orElseThrow(() -> new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), "Person not found"));
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving person by ID: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public PersonDomain update(PersonDomain domain) {
        try {
            logger.info("Attempting to update person: {}", domain);
            if (!repository.existsById(domain.getId())) {
                throw new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Person not found");
            }
            PersonEntity entity = PersonEntity.fromDomain(domain);
            PersonEntity updatedEntity = repository.save(entity);
            logger.info("Person updated successfully: {}", updatedEntity.toDomain());
            return updatedEntity.toDomain();
        } catch (DataBaseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating person: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            logger.info("Attempting to delete person by ID: {}", id);
            repository.deleteById(id);
            logger.info("Person deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting person: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDomain findByIdentification(String identification){
        try {
            logger.info("Retrieving person by identification: {}", identification);
            return repository.findByIdentification(identification)
                    .map(PersonEntity::toDomain)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error retrieving person by ID: {}", e.getMessage());
            throw new DataBaseException(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorMessage(), e);
        }
    }
}
