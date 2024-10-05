package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.adapter.postgres.models.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountPostgresRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByClientId(UUID clientId);


}
