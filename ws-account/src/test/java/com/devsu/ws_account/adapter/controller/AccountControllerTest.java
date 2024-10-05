package com.devsu.ws_account.adapter.controller;

import com.devsu.ws_account.adapter.controller.models.CreateAccountRequest;
import com.devsu.ws_account.adapter.controller.models.UpdateAccountRequest;
import com.devsu.ws_account.application.port.in.account.*;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.AccountTypeDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAccountPort createAccountPort;

    @MockBean
    private GetAllAccountsPort getAllAccountsPort;

    @MockBean
    private GetAccountByIdPort getAccountByIdPort;

    @MockBean
    private UpdateAccountPort updateAccountPort;

    @MockBean
    private DeleteAccountPort deleteAccountPort;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateAccountRequest createAccountRequest;
    private UpdateAccountRequest updateAccountRequest;
    private AccountDomain accountDomain;
    private UUID accountId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        createAccountRequest = CreateAccountRequest.builder()
                .accountNumber("2254871234")
                .accountType(1)
                .initialBalance(new BigDecimal("1000.00"))
                .clientId(clientId.toString())
                .build();

        updateAccountRequest = UpdateAccountRequest.builder()
                .id(accountId.toString())
                .accountNumber("2254871234")
                .accountType(1)
                .initialBalance(new BigDecimal("1500.00"))
                .status(true)
                .clientId(clientId.toString())
                .build();

        accountDomain = AccountDomain.builder()
                .id(accountId)
                .accountNumber(createAccountRequest.getAccountNumber())
                .accountType(AccountTypeDomain.builder()
                        .id(createAccountRequest.getAccountType())
                        .name("Corriente")
                        .build())
                .initialBalance(createAccountRequest.getInitialBalance())
                .status(true)
                .clientId(UUID.fromString(createAccountRequest.getClientId()))
                .build();
    }

    @Test
    void testCreateAccountSuccess() throws Exception {
        when(createAccountPort.create(any(AccountDomain.class))).thenReturn(accountDomain);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(201))
                .andExpect(jsonPath("$.message").value("Created"))
                .andExpect(jsonPath("$.data.accountNumber").value("2254871234"))
                .andExpect(jsonPath("$.data.accountType.name").value("Corriente"));

        verify(createAccountPort, times(1)).create(any(AccountDomain.class));
    }

    @Test
    void testCreateAccountValidationErrors() throws Exception {
        CreateAccountRequest invalidRequest = CreateAccountRequest.builder()
                .accountNumber("")
                .accountType(0)
                .initialBalance(new BigDecimal("-100.00"))
                .clientId("invalid-uuid")
                .build();

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(createAccountPort, never()).create(any(AccountDomain.class));
    }

    @Test
    void testCreateAccountDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode(),
                "Failed to create account"
        );

        when(createAccountPort.create(any(AccountDomain.class))).thenThrow(dbException);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorMessage()));

        verify(createAccountPort, times(1)).create(any(AccountDomain.class));
    }

    @Test
    void testCreateAccountUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(createAccountPort.create(any(AccountDomain.class))).thenThrow(exception);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorMessage()));

        verify(createAccountPort, times(1)).create(any(AccountDomain.class));
    }

    @Test
    void testGetAllAccountsSuccess() throws Exception {
        List<AccountDomain> accounts = Collections.singletonList(accountDomain);
        when(getAllAccountsPort.getAll()).thenReturn(accounts);

        mockMvc.perform(get("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].accountNumber").value("2254871234"));

        verify(getAllAccountsPort, times(1)).getAll();
    }

    @Test
    void testGetAllAccountsDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                "Failed to retrieve accounts"
        );

        when(getAllAccountsPort.getAll()).thenThrow(dbException);

        mockMvc.perform(get("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorMessage()));

        verify(getAllAccountsPort, times(1)).getAll();
    }

    @Test
    void testGetAccountByIdSuccess() throws Exception {
        when(getAccountByIdPort.getById(accountId)).thenReturn(accountDomain);

        mockMvc.perform(get("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accountNumber").value("2254871234"));

        verify(getAccountByIdPort, times(1)).getById(accountId);
    }

    @Test
    void testGetAccountByIdNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode(),
                "Account not found"
        );

        when(getAccountByIdPort.getById(accountId)).thenThrow(dbException);

        mockMvc.perform(get("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorMessage()));

        verify(getAccountByIdPort, times(1)).getById(accountId);
    }

    @Test
    void testUpdateAccountSuccess() throws Exception {
        when(updateAccountPort.update(any(AccountDomain.class))).thenReturn(accountDomain);

        mockMvc.perform(put("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accountNumber").value("2254871234"))
                .andExpect(jsonPath("$.data.initialBalance").value(1000.00));

        verify(updateAccountPort, times(1)).update(any(AccountDomain.class));
    }

    @Test
    void testUpdateAccountValidationErrors() throws Exception {
        UpdateAccountRequest invalidRequest = UpdateAccountRequest.builder()
                .id("invalid-uuid")
                .accountNumber("")
                .accountType(0)
                .initialBalance(new BigDecimal("-100.00"))
                .status(null)
                .clientId("invalid-uuid")
                .build();

        mockMvc.perform(put("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(updateAccountPort, never()).update(any(AccountDomain.class));
    }

    @Test
    void testUpdateAccountDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode(),
                "Failed to update account"
        );

        when(updateAccountPort.update(any(AccountDomain.class))).thenThrow(dbException);

        mockMvc.perform(put("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorMessage()));

        verify(updateAccountPort, times(1)).update(any(AccountDomain.class));
    }

    @Test
    void testUpdateAccountUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(updateAccountPort.update(any(AccountDomain.class))).thenThrow(exception);

        mockMvc.perform(put("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorMessage()));

        verify(updateAccountPort, times(1)).update(any(AccountDomain.class));
    }

    @Test
    void testDeleteAccountSuccess() throws Exception {
        doNothing().when(deleteAccountPort).delete(accountId);

        mockMvc.perform(delete("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteAccountPort, times(1)).delete(accountId);
    }

    @Test
    void testDeleteAccountNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode(),
                "Account not found"
        );

        doThrow(dbException).when(deleteAccountPort).delete(accountId);

        mockMvc.perform(delete("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorMessage()));

        verify(deleteAccountPort, times(1)).delete(accountId);
    }

    @Test
    void testDeleteAccountUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        doThrow(exception).when(deleteAccountPort).delete(accountId);

        mockMvc.perform(delete("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorMessage()));

        verify(deleteAccountPort, times(1)).delete(accountId);
    }
}
