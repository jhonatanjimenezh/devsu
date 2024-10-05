package com.devsu.ws_customer.application.port.in;

import com.devsu.ws_customer.domain.ClientDomain;
import org.springframework.data.domain.Page;

public interface GetAllClientsPaginatedPort {

    Page<ClientDomain> getAllPaginated(int page, int size);
}
