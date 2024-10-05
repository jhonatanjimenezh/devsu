package com.devsu.ws_customer.application.port.out;

import com.devsu.ws_customer.domain.ClientDomain;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ClientStorageRepository {

    ClientDomain save(ClientDomain domain);
    List<ClientDomain> getAll();
    ClientDomain getById(UUID id);
    Page<ClientDomain> getAllPaginated(int page, int size);
    ClientDomain update(ClientDomain domain);
    void delete(UUID id);
    ClientDomain findByClientId(String clientId);
}
