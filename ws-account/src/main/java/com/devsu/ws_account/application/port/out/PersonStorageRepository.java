package com.devsu.ws_account.application.port.out;

import com.devsu.ws_account.domain.PersonDomain;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonStorageRepository {

    PersonDomain save(PersonDomain domain);
    List<PersonDomain> getAll();
    PersonDomain getById(Long id);
    Page<PersonDomain> getAllPaginated(int page, int size);
    PersonDomain update(PersonDomain domain);
    void delete(Long id);
}
