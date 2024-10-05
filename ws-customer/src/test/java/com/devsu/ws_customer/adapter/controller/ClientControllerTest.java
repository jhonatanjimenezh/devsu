package com.devsu.ws_customer.adapter.controller;

import com.devsu.ws_customer.adapter.controller.models.ClientUpdateRequest;
import com.devsu.ws_customer.adapter.controller.models.CreateClientRequest;
import com.devsu.ws_customer.application.port.in.*;
import com.devsu.ws_customer.config.exception.DataBaseException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import com.devsu.ws_customer.domain.GenderDomain;
import com.devsu.ws_customer.domain.PersonDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateClientPort createClientPort;

    @MockBean
    private GetAllClientsPort getAllClientsPort;

    @MockBean
    private GetClientByIdPort getClientByIdPort;

    @MockBean
    private UpdateClientPort updateClientPort;

    @MockBean
    private DeleteClientPort deleteClientPort;

    @MockBean
    private GetAllClientsPaginatedPort getAllClientsPaginatedPort;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateClientRequest createClientRequest;
    private ClientUpdateRequest clientUpdateRequest;
    private ClientDomain clientDomain;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        createClientRequest = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("123456789")
                .address("123 Main St")
                .phoneNumber("5551234567")
                .clientId("client123")
                .password("password")
                .build();

        clientUpdateRequest = ClientUpdateRequest.builder()
                .id(clientId)
                .name("John Doe Updated")
                .gender(1)
                .age(31)
                .identification("123456789")
                .address("456 Elm St")
                .phoneNumber("5557654321")
                .clientId("client123")
                .password("newpassword")
                .status(true)
                .build();

        clientDomain = ClientDomain.builder()
                .id(clientId)
                .clientId(createClientRequest.getClientId())
                .password(createClientRequest.getPassword())
                .status(true)
                .person(PersonDomain.builder()
                        .id(UUID.randomUUID())
                        .name(createClientRequest.getName())
                        .gender(new GenderDomain(createClientRequest.getGender(), "MALE"))
                        .age(createClientRequest.getAge())
                        .identification(createClientRequest.getIdentification())
                        .address(createClientRequest.getAddress())
                        .phone(createClientRequest.getPhoneNumber())
                        .build())
                .build();
    }



    @Test
    void testCreateClientSuccess() throws Exception {
        when(createClientPort.create(any(ClientDomain.class))).thenReturn(clientDomain);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(201))
                .andExpect(jsonPath("$.message").value("Created"))
                .andExpect(jsonPath("$.data.clientId").value("client123"))
                .andExpect(jsonPath("$.data.person.name").value("John Doe"));

        verify(createClientPort, times(1)).create(any(ClientDomain.class));
    }

    @Test
    void testCreateClientValidationErrors() throws Exception {
        CreateClientRequest invalidRequest = CreateClientRequest.builder()
                .name("")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(createClientPort, never()).create(any(ClientDomain.class));
    }

    @Test
    void testCreateClientDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_CREATE_ERROR.getErrorCode(),
                "Failed to create client"
        );

        when(createClientPort.create(any(ClientDomain.class))).thenThrow(dbException);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_CREATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_CREATE_ERROR.getErrorMessage()));

        verify(createClientPort, times(1)).create(any(ClientDomain.class));
    }

    @Test
    void testCreateClientUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(createClientPort.create(any(ClientDomain.class))).thenThrow(exception);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.GENERIC_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.GENERIC_ERROR.getErrorMessage()));

        verify(createClientPort, times(1)).create(any(ClientDomain.class));
    }

    @Test
    void testGetAllClientsSuccess() throws Exception {
        List<ClientDomain> clients = Collections.singletonList(clientDomain);
        when(getAllClientsPort.getAll()).thenReturn(clients);

        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].clientId").value("client123"));

        verify(getAllClientsPort, times(1)).getAll();
    }

    @Test
    void testGetAllClientsDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                "Failed to retrieve all clients"
        );

        when(getAllClientsPort.getAll()).thenThrow(dbException);

        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorMessage()));

        verify(getAllClientsPort, times(1)).getAll();
    }

    @Test
    void testGetClientByIdSuccess() throws Exception {
        when(getClientByIdPort.getById(clientId)).thenReturn(clientDomain);

        mockMvc.perform(get("/clientes/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.clientId").value("client123"));

        verify(getClientByIdPort, times(1)).getById(clientId);
    }

    @Test
    void testGetClientByIdNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode(),
                "Client not found"
        );

        when(getClientByIdPort.getById(clientId)).thenThrow(dbException);

        mockMvc.perform(get("/clientes/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_FIND_BY_ID_ERROR.getErrorMessage()));

        verify(getClientByIdPort, times(1)).getById(clientId);
    }

    @Test
    void testUpdateClientSuccess() throws Exception {
        when(updateClientPort.update(any(ClientDomain.class))).thenReturn(clientDomain);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.clientId").value("client123"))
                .andExpect(jsonPath("$.data.person.name").value("John Doe"));

        verify(updateClientPort, times(1)).update(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientValidationErrors() throws Exception {
        ClientUpdateRequest invalidRequest = ClientUpdateRequest.builder()
                .name("")
                .build();

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(updateClientPort, never()).update(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR.getErrorCode(),
                "Failed to update client"
        );

        when(updateClientPort.update(any(ClientDomain.class))).thenThrow(dbException);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_UPDATE_ERROR.getErrorMessage()));

        verify(updateClientPort, times(1)).update(any(ClientDomain.class));
    }

    @Test
    void testUpdateClientUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(updateClientPort.update(any(ClientDomain.class))).thenThrow(exception);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.GENERIC_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.GENERIC_ERROR.getErrorMessage()));

        verify(updateClientPort, times(1)).update(any(ClientDomain.class));
    }

    @Test
    void testDeleteClientSuccess() throws Exception {
        doNothing().when(deleteClientPort).delete(clientId);

        mockMvc.perform(delete("/clientes/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteClientPort, times(1)).delete(clientId);
    }

    @Test
    void testDeleteClientNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_DELETE_ERROR.getErrorCode(),
                "Client not found"
        );

        doThrow(dbException).when(deleteClientPort).delete(clientId);

        mockMvc.perform(delete("/clientes/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_DELETE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_DELETE_ERROR.getErrorMessage()));

        verify(deleteClientPort, times(1)).delete(clientId);
    }

    @Test
    void testGetAllClientsPaginatedSuccess() throws Exception {
        List<ClientDomain> clients = Collections.singletonList(clientDomain);
        Page<ClientDomain> paginatedClients = new PageImpl<>(clients);

        when(getAllClientsPaginatedPort.getAllPaginated(0, 10)).thenReturn(paginatedClients);

        mockMvc.perform(get("/clientes/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].clientId").value("client123"));

        verify(getAllClientsPaginatedPort, times(1)).getAllPaginated(0, 10);
    }

    @Test
    void testGetAllClientsPaginatedDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                "Failed to retrieve paginated clients"
        );

        when(getAllClientsPaginatedPort.getAllPaginated(0, 10)).thenThrow(dbException);

        mockMvc.perform(get("/clientes/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.CUSTOMER_CONTROLLER_FIND_ALL_ERROR.getErrorMessage()));

        verify(getAllClientsPaginatedPort, times(1)).getAllPaginated(0, 10);
    }

    @Test
    void testGetAllClientsPaginatedUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(getAllClientsPaginatedPort.getAllPaginated(0, 10)).thenThrow(exception);

        mockMvc.perform(get("/clientes/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.GENERIC_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.GENERIC_ERROR.getErrorMessage()));

        verify(getAllClientsPaginatedPort, times(1)).getAllPaginated(0, 10);
    }

}
