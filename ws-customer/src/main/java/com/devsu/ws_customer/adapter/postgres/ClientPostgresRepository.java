package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientPostgresRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByClientId(String clientId);
}


