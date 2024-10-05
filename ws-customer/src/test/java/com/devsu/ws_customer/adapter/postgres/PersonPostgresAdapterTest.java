package com.devsu.ws_customer.adapter.postgres;

import com.devsu.ws_customer.adapter.postgres.models.PersonEntity;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class PersonPostgresAdapterTest {

    @Autowired
    private PersonPostgresAdapter adapter;

    @MockBean
    private PersonPostgresRepository repository;

    private PersonDomain personDomain;
    private PersonEntity personEntity;

    @BeforeEach
    void setUp() {
        personDomain = new PersonDomain(UUID.randomUUID(), "John Doe", new GenderDomain(1, "OTHER"), 30, "123", "country", "1234567890");
        personEntity = PersonEntity.fromDomain(personDomain);
    }

    @Test
    void testSavePersonSuccess() {
        when(repository.save(any(PersonEntity.class))).thenReturn(personEntity);

        PersonDomain result = adapter.save(personDomain);

        assertNotNull(result);
        assertEquals(personDomain.getName(), result.getName());
        verify(repository, times(1)).save(any(PersonEntity.class));
    }

    @Test
    void testSavePersonFailure() {
        when(repository.save(any(PersonEntity.class))).thenThrow(new RuntimeException("Error saving person"));

        assertThrows(DataBaseException.class, () -> adapter.save(personDomain));
        verify(repository, times(1)).save(any(PersonEntity.class));
    }

    @Test
    void testSavePersonReturnsNull() {
        when(repository.save(any(PersonEntity.class))).thenReturn(null);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.save(personDomain));
        assertEquals(SPError.CUSTOMER_ADAPTER_SAVE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).save(any(PersonEntity.class));
    }

    @Test
    void testGetPersonByIdSuccess() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(personEntity));

        PersonDomain result = adapter.getById(personDomain.getId());

        assertNotNull(result);
        assertEquals(personDomain.getName(), result.getName());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testGetPersonByIdFailure() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.getById(personDomain.getId()));
        assertEquals(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testGetPersonByIdException() {
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.getById(personDomain.getId()));
        assertEquals(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdatePersonSuccess() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(repository.save(any(PersonEntity.class))).thenReturn(personEntity);

        PersonDomain result = adapter.update(personDomain);

        assertNotNull(result);
        assertEquals(personDomain.getName(), result.getName());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).save(any(PersonEntity.class));
    }

    @Test
    void testUpdatePersonNotFound() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.update(personDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        assertEquals("Person not found", exception.getMessage());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, never()).save(any(PersonEntity.class));
    }

    @Test
    void testUpdatePersonFailure() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(repository.save(any(PersonEntity.class))).thenThrow(new RuntimeException("Error updating person"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.update(personDomain));

        assertEquals(SPError.CUSTOMER_ADAPTER_UPDATE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).save(any(PersonEntity.class));
    }

    @Test
    void testDeletePersonSuccess() {
        doNothing().when(repository).deleteById(any(UUID.class));

        assertDoesNotThrow(() -> adapter.delete(personDomain.getId()));

        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testDeletePersonFailure() {
        doThrow(new RuntimeException("Error deleting person")).when(repository).deleteById(any(UUID.class));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.delete(personDomain.getId()));

        assertEquals(SPError.CUSTOMER_ADAPTER_DELETE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void testFindByIdentificationSuccess() {
        when(repository.findByIdentification(anyString())).thenReturn(Optional.of(personEntity));

        PersonDomain result = adapter.findByIdentification("123");

        assertNotNull(result);
        assertEquals(personDomain.getName(), result.getName());
        verify(repository, times(1)).findByIdentification(anyString());
    }

    @Test
    void testFindByIdentificationNotFound() {
        when(repository.findByIdentification(anyString())).thenReturn(Optional.empty());

        PersonDomain result = adapter.findByIdentification("123");

        assertNull(result);
        verify(repository, times(1)).findByIdentification(anyString());
    }

    @Test
    void testFindByIdentificationFailure() {
        when(repository.findByIdentification(anyString())).thenThrow(new RuntimeException("Error finding person"));

        DataBaseException exception = assertThrows(DataBaseException.class, () -> adapter.findByIdentification("123"));

        assertEquals(SPError.CUSTOMER_ADAPTER_FIND_ERROR.getErrorCode(), exception.getErrorCode());
        verify(repository, times(1)).findByIdentification(anyString());
    }
}
