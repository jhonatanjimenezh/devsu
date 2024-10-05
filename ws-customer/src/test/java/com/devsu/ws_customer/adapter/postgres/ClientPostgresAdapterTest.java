package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.ClientEntity;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ClientPostgresAdapterTest {

    @Autowired
    private ClientPostgresAdapter adapter;

    @MockBean
    private ClientPostgresRepository repository;

    private ClientDomain clientDomain;
    private ClientEntity clientEntity;
    private PersonDomain personDomain;

    @BeforeEach
    void setUp() {
        personDomain = new PersonDomain(UUID.randomUUID(), "John Doe", new GenderDomain(1, "OTHER"), 30, "123", "country", "1234567890");
        clientDomain = new ClientDomain(UUID.randomUUID(), "client123", "123", true, personDomain);
        clientEntity = ClientEntity.fromDomain(clientDomain);
    }

    @Test
    void testSaveClientSuccess() {
        when(repository.save(any(ClientEntity.class))).thenReturn(clientEntity);

        ClientDomain result = adapter.save(clientDomain);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(repository, times(1)).save(any(ClientEntity.class));
    }

    @Test
    void testSaveClientFailure() {
        when(repository.save(any(ClientEntity.class))).thenThrow(new RuntimeException("Error saving client"));

        assertThrows(DataBaseException.class, () -> adapter.save(clientDomain));
        verify(repository, times(1)).save(any(ClientEntity.class));
    }

    @Test
    void testGetClientByIdSuccess() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(clientEntity));

        ClientDomain result = adapter.getById(clientDomain.getId());

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testGetClientByIdFailure() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(DataBaseException.class, () -> adapter.getById(clientDomain.getId()));
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveClientReturnsNull() {
        when(repository.save(any(ClientEntity.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.save(clientDomain));
        assertEquals(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).save(any(ClientEntity.class));
    }

    @Test
    void testGetClientByIdException() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.getById(clientDomain.getId()));
        assertEquals(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateClientNotFound() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.update(clientDomain));
        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Client not found", exception.getMessage());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, never()).save(any(ClientEntity.class));
    }

    @Test
    void testDeleteClientSuccess() {
        doNothing().when(repository).deleteById(any(UUID.class));

        assertDoesNotThrow(() -> adapter.delete(clientDomain.getId()));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testDeleteClientFailure() {
        doThrow(new RuntimeException("Error deleting client")).when(repository).deleteById(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.delete(clientDomain.getId()));
        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testFindByClientIdNotFound() {
        when(repository.findByClientId(anyString())).thenReturn(Optional.empty());

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.findByClientId("client123"));
        assertEquals(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findByClientId(anyString());
    }

    @Test
    void testGetAllPaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientEntity> page = mock(Page.class);
        when(repository.findAll(pageable)).thenReturn(page);

        when(page.map(any())).thenAnswer(invocation -> {
            return Page.empty();
        });

        Page<ClientDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllPaginatedWithData() {
        Pageable pageable = PageRequest.of(0, 10);
        ClientEntity clientEntity = ClientEntity.fromDomain(clientDomain);

        Page<ClientEntity> page = new PageImpl<>(List.of(clientEntity));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<ClientDomain> result = adapter.getAllPaginated(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(clientDomain.getClientId(), result.getContent().get(0).getClientId());
        verify(repository, times(1)).findAll(pageable);
    }

}
