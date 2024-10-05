package com.devsu.ws_customer.adapter.controller;

import com.devsu.ws_customer.adapter.controller.models.ClientUpdateRequest;
import com.devsu.ws_customer.adapter.controller.models.CreateClientRequest;
import com.devsu.ws_customer.adapter.postgres.ClientPostgresRepository;
import com.devsu.ws_customer.adapter.postgres.models.ClientEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientPostgresRepository clientRepository;

    @BeforeEach
    public void setUp() {
        clientRepository.deleteAll();
    }

    @Test
    void createClient_shouldReturn201AndSaveClient() throws Exception {
        CreateClientRequest request = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("1234567890")
                .address("123 Main Street")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("securePassword")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.clientId").value("jdoe123"));

        ClientEntity savedClient = clientRepository.findByClientId("jdoe123").orElse(null);
        assert savedClient != null;
        assert savedClient.getPerson().getName().equals("John Doe");
    }

    @Test
    void getAllClients_shouldReturn200AndClientsList() throws Exception {
        CreateClientRequest request1 = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("1234567890")
                .address("123 Main Street")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("securePassword")
                .build();

        CreateClientRequest request2 = CreateClientRequest.builder()
                .name("Jane Smith")
                .gender(2)
                .age(25)
                .identification("0987654321")
                .address("456 Elm Street")
                .phoneNumber("0987654321")
                .clientId("jsmith456")
                .password("password123")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(get("/clientes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].clientId").value("jdoe123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].clientId").value("jsmith456"));
    }

    @Test
    void getClientById_shouldReturn200AndClient() throws Exception {
        CreateClientRequest request = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("1234567890")
                .address("123 Main Street")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("securePassword")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ClientEntity savedClient = clientRepository.findByClientId("jdoe123").orElse(null);
        assert savedClient != null;

        mockMvc.perform(get("/clientes/" + savedClient.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.clientId").value("jdoe123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.person.name").value("John Doe"));
    }

    @Test
    void updateClient_shouldReturn200AndUpdateClient() throws Exception {
        CreateClientRequest request = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("1234567890")
                .address("123 Main Street")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("securePassword")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ClientEntity savedClient = clientRepository.findByClientId("jdoe123").orElse(null);
        assert savedClient != null;

        ClientUpdateRequest updateRequest = ClientUpdateRequest.builder()
                .id(savedClient.getId())
                .name("John Doe Updated")
                .gender(1)
                .age(35)
                .identification("1234567890")
                .address("Updated Address")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("newSecurePassword")
                .status(true)
                .build();

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.person.name").value("John Doe Updated"));

        ClientEntity updatedClient = clientRepository.findById(savedClient.getId()).orElse(null);
        assert updatedClient != null;
        assert updatedClient.getPerson().getName().equals("John Doe Updated");
    }

    @Test
    void deleteClient_shouldReturn204AndDeleteClient() throws Exception {
        CreateClientRequest request = CreateClientRequest.builder()
                .name("John Doe")
                .gender(1)
                .age(30)
                .identification("1234567890")
                .address("123 Main Street")
                .phoneNumber("1234567890")
                .clientId("jdoe123")
                .password("securePassword")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ClientEntity savedClient = clientRepository.findByClientId("jdoe123").orElse(null);
        assert savedClient != null;

        mockMvc.perform(delete("/clientes/" + savedClient.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assert clientRepository.findById(savedClient.getId()).isEmpty();
    }
}
