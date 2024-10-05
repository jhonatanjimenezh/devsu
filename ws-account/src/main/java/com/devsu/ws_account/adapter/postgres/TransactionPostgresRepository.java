package com.devsu.ws_account.adapter.postgres;

import com.devsu.ws_account.adapter.postgres.models.AccountEntity;
import com.devsu.ws_account.adapter.postgres.models.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionPostgresRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByAccount(AccountEntity account);

    TransactionEntity findTopByAccountOrderByDateDesc(AccountEntity account);

}
