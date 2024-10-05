package com.devsu.ws_customer.application.port.out;

import com.devsu.ws_customer.domain.PersonDomain;

import java.util.UUID;

public interface PersonStorageRepository {

    PersonDomain save(PersonDomain domain);
    PersonDomain getById(UUID id);
    PersonDomain update(PersonDomain domain);
    void delete(UUID id);
    PersonDomain findByIdentification(String identification);
}
