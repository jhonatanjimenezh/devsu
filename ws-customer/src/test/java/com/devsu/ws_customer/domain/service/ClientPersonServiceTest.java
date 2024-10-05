package com.devsu.ws_customer.domain.service;

import com.devsu.ws_customer.adapter.postgres.ClientPostgresAdapter;
import com.devsu.ws_customer.adapter.postgres.PersonPostgresAdapter;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClientPersonServiceTest {

    @Mock
    private ClientPostgresAdapter clientAdapter;

    @Mock
    private PersonPostgresAdapter personAdapter;

    private ClientPersonService clientPersonService;

    private ClientDomain clientDomain;
    private PersonDomain personDomain;
    private GenderDomain genderDomain;

    @BeforeEach
    void setUp() {
        clientPersonService = new ClientPersonService(clientAdapter, personAdapter);

        genderDomain = new GenderDomain(1, "OTHER");
        personDomain = new PersonDomain(
                UUID.randomUUID(),
                "John Doe",
                genderDomain,
                30,
                "123",
                "country",
                "1234567890"
        );
        clientDomain = new ClientDomain(
                UUID.randomUUID(),
                "client123",
                "password",
                true,
                personDomain
        );
    }

    @Test
    void testCreateClientSuccess() {
        when(personAdapter.findByIdentification(anyString())).thenReturn(null);
        when(personAdapter.save(any(PersonDomain.class))).thenReturn(personDomain);
        when(clientAdapter.save(any(ClientDomain.class))).thenReturn(clientDomain);

        ClientDomain result = clientPersonService.create(clientDomain);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(personAdapter, times(1)).findByIdentification(anyString());
        verify(personAdapter, times(1)).save(any(PersonDomain.class));
        verify(clientAdapter, times(1)).save(any(ClientDomain.class));
    }

    @Test
    void testCreateClientPersonAlreadyExists() {
        when(personAdapter.findByIdentification(anyString())).thenReturn(personDomain);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.create(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Person already exists", exception.getMessage());
        verify(personAdapter, times(1)).findByIdentification(anyString());
        verify(personAdapter, never()).save(any(PersonDomain.class));
        verify(clientAdapter, never()).save(any(ClientDomain.class));
    }

    @Test
    void testCreateClientPersonSaveFailure() {
        when(personAdapter.findByIdentification(anyString())).thenReturn(null);
        when(personAdapter.save(any(PersonDomain.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), "Error saving person"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.create(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error saving person", exception.getMessage());
        verify(personAdapter, times(1)).findByIdentification(anyString());
        verify(personAdapter, times(1)).save(any(PersonDomain.class));
        verify(clientAdapter, never()).save(any(ClientDomain.class));
    }

    @Test
    void testCreateClientClientSaveFailure() {
        when(personAdapter.findByIdentification(anyString())).thenReturn(null);
        when(personAdapter.save(any(PersonDomain.class))).thenReturn(personDomain);
        when(clientAdapter.save(any(ClientDomain.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), "Error saving client"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.create(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error saving client", exception.getMessage());
        verify(personAdapter, times(1)).findByIdentification(anyString());
        verify(personAdapter, times(1)).save(any(PersonDomain.class));
        verify(clientAdapter, times(1)).save(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientSuccess() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        when(personAdapter.update(any(PersonDomain.class))).thenReturn(personDomain);
        when(clientAdapter.update(any(ClientDomain.class))).thenReturn(clientDomain);

        ClientDomain result = clientPersonService.update(clientDomain);

        assertNotNull(result);
        assertEquals(clientDomain.getClientId(), result.getClientId());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).update(any(PersonDomain.class));
        verify(clientAdapter, times(1)).update(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientNotFound() {
        when(clientAdapter.getById(any(UUID.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Client not found"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.update(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Client not found", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, never()).getById(any(UUID.class));
        verify(personAdapter, never()).update(any(PersonDomain.class));
        verify(clientAdapter, never()).update(any(ClientDomain.class));
    }

    @Test
    void testUpdatePersonNotFound() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Person not found"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.update(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Person not found", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, never()).update(any(PersonDomain.class));
        verify(clientAdapter, never()).update(any(ClientDomain.class));
    }

    @Test
    void testUpdatePersonFailure() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        when(personAdapter.update(any(PersonDomain.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Error updating person"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.update(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error updating person", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).update(any(PersonDomain.class));
        verify(clientAdapter, never()).update(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientFailure() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        when(personAdapter.update(any(PersonDomain.class))).thenReturn(personDomain);
        when(clientAdapter.update(any(ClientDomain.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), "Error updating client"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.update(clientDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error updating client", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).update(any(PersonDomain.class));
        verify(clientAdapter, times(1)).update(any(ClientDomain.class));
    }

    @Test
    void testDeleteClientSuccess() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        doNothing().when(clientAdapter).delete(any(UUID.class));
        doNothing().when(personAdapter).delete(any(UUID.class));

        assertDoesNotThrow(() -> clientPersonService.delete(clientDomain.getId()));

        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(clientAdapter, times(1)).delete(any(UUID.class));
        verify(personAdapter, times(1)).delete(any(UUID.class));
    }

    @Test
    void testDeleteClientNotFound() {
        when(clientAdapter.getById(any(UUID.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), "Client not found"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.delete(clientDomain.getId()));

        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Client not found", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, never()).getById(any(UUID.class));
        verify(clientAdapter, never()).delete(any(UUID.class));
        verify(personAdapter, never()).delete(any(UUID.class));
    }

    @Test
    void testDeletePersonNotFound() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), "Person not found"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.delete(clientDomain.getId()));

        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Person not found", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(clientAdapter, never()).delete(any(UUID.class));
        verify(personAdapter, never()).delete(any(UUID.class));
    }

    @Test
    void testDeleteClientFailure() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        doThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), "Error deleting client")).when(clientAdapter).delete(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.delete(clientDomain.getId()));

        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error deleting client", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(clientAdapter, times(1)).delete(any(UUID.class));
        verify(personAdapter, never()).delete(any(UUID.class));
    }

    @Test
    void testDeletePersonFailure() {
        when(clientAdapter.getById(any(UUID.class))).thenReturn(clientDomain);
        when(personAdapter.getById(any(UUID.class))).thenReturn(personDomain);
        doNothing().when(clientAdapter).delete(any(UUID.class));
        doThrow(new DataBaseException(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), "Error deleting person")).when(personAdapter).delete(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> clientPersonService.delete(clientDomain.getId()));

        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Error deleting person", exception.getMessage());
        verify(clientAdapter, times(1)).getById(any(UUID.class));
        verify(personAdapter, times(1)).getById(any(UUID.class));
        verify(clientAdapter, times(1)).delete(any(UUID.class));
        verify(personAdapter, times(1)).delete(any(UUID.class));
    }
}
