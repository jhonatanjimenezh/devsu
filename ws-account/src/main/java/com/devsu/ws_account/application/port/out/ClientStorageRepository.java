package com.devsu.ws_account.application.port.out;

import com.devsu.ws_account.domain.ClientDomain;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientStorageRepository {

    ClientDomain save(ClientDomain domain);
    List<ClientDomain> getAll();
    ClientDomain getById(Long id);
    Page<ClientDomain> getAllPaginated(int page, int size);
    ClientDomain update(ClientDomain domain);
    void delete(Long id);
}
