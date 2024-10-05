package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonPostgresRepository extends JpaRepository<PersonEntity, UUID> {
    Optional<PersonEntity> findByIdentification(String identification);
}