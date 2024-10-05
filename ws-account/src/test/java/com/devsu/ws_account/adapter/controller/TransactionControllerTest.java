package com.devsu.ws_account.adapter.controller;

import com.devsu.ws_account.adapter.controller.models.CreateTransactionRequest;
import com.devsu.ws_account.adapter.controller.models.UpdateTransactionRequest;
import com.devsu.ws_account.application.port.in.transaction.*;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.TransactionDomain;
import com.devsu.ws_account.domain.TransactionTypeDomain;
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

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTransactionPort createTransactionPort;

    @MockBean
    private GetAllTransactionsPort getAllTransactionsPort;

    @MockBean
    private GetTransactionByIdPort getTransactionByIdPort;

    @MockBean
    private UpdateTransactionPort updateTransactionPort;

    @MockBean
    private DeleteTransactionPort deleteTransactionPort;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateTransactionRequest createTransactionRequest;
    private UpdateTransactionRequest updateTransactionRequest;
    private TransactionDomain transactionDomain;
    private UUID transactionId;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        createTransactionRequest = CreateTransactionRequest.builder()
                .transactionType(1)
                .amount(new BigDecimal("500.00"))
                .accountId(accountId.toString())
                .build();

        updateTransactionRequest = UpdateTransactionRequest.builder()
                .id(transactionId.toString())
                .transactionType(1)
                .amount(new BigDecimal("600.00"))
                .accountId(accountId.toString())
                .build();

        transactionDomain = TransactionDomain.builder()
                .id(transactionId)
                .date(new Date())
                .transactionType(TransactionTypeDomain.builder()
                        .id(createTransactionRequest.getTransactionType())
                        .name("Depósito")
                        .build())
                .amount(createTransactionRequest.getAmount())
                .balance(new BigDecimal("1500.00"))
                .account(AccountDomain.builder()
                        .id(UUID.fromString(createTransactionRequest.getAccountId()))
                        .accountNumber("2254871234")
                        .build())
                .build();
    }

    @Test
    void testCreateTransactionSuccess() throws Exception {
        when(createTransactionPort.create(any(TransactionDomain.class))).thenReturn(transactionDomain);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(201))
                .andExpect(jsonPath("$.message").value("Created"))
                .andExpect(jsonPath("$.data.amount").value(500.00))
                .andExpect(jsonPath("$.data.transactionType.name").value("Depósito"));

        verify(createTransactionPort, times(1)).create(any(TransactionDomain.class));
    }

    @Test
    void testCreateTransactionValidationErrors() throws Exception {
        CreateTransactionRequest invalidRequest = CreateTransactionRequest.builder()
                .transactionType(0)
                .amount(new BigDecimal("-100.00"))
                .accountId("invalid-uuid")
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(createTransactionPort, never()).create(any(TransactionDomain.class));
    }

    @Test
    void testCreateTransactionDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode(),
                "Failed to create transaction"
        );

        when(createTransactionPort.create(any(TransactionDomain.class))).thenThrow(dbException);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorMessage()));

        verify(createTransactionPort, times(1)).create(any(TransactionDomain.class));
    }

    @Test
    void testCreateTransactionUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(createTransactionPort.create(any(TransactionDomain.class))).thenThrow(exception);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR.getErrorMessage()));

        verify(createTransactionPort, times(1)).create(any(TransactionDomain.class));
    }

    @Test
    void testGetAllTransactionsSuccess() throws Exception {
        List<TransactionDomain> transactions = Collections.singletonList(transactionDomain);
        when(getAllTransactionsPort.getAll()).thenReturn(transactions);

        mockMvc.perform(get("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].amount").value(500.00));

        verify(getAllTransactionsPort, times(1)).getAll();
    }

    @Test
    void testGetAllTransactionsDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorCode(),
                "Failed to retrieve transactions"
        );

        when(getAllTransactionsPort.getAll()).thenThrow(dbException);

        mockMvc.perform(get("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR.getErrorMessage()));

        verify(getAllTransactionsPort, times(1)).getAll();
    }

    @Test
    void testGetTransactionByIdSuccess() throws Exception {
        when(getTransactionByIdPort.getById(transactionId)).thenReturn(transactionDomain);

        mockMvc.perform(get("/movimientos/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.amount").value(500.00));

        verify(getTransactionByIdPort, times(1)).getById(transactionId);
    }

    @Test
    void testGetTransactionByIdNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode(),
                "Transaction not found"
        );

        when(getTransactionByIdPort.getById(transactionId)).thenThrow(dbException);

        mockMvc.perform(get("/movimientos/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR.getErrorMessage()));

        verify(getTransactionByIdPort, times(1)).getById(transactionId);
    }

    @Test
    void testUpdateTransactionSuccess() throws Exception {
        when(updateTransactionPort.update(any(TransactionDomain.class))).thenReturn(transactionDomain);

        mockMvc.perform(put("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code_status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.amount").value(500.00));

        verify(updateTransactionPort, times(1)).update(any(TransactionDomain.class));
    }

    @Test
    void testUpdateTransactionValidationErrors() throws Exception {
        UpdateTransactionRequest invalidRequest = UpdateTransactionRequest.builder()
                .id("invalid-uuid")
                .transactionType(0)
                .amount(new BigDecimal("-100.00"))
                .accountId("invalid-uuid")
                .build();

        mockMvc.perform(put("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameters provided in the request"))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));

        verify(updateTransactionPort, never()).update(any(TransactionDomain.class));
    }

    @Test
    void testUpdateTransactionDataBaseException() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode(),
                "Failed to update transaction"
        );

        when(updateTransactionPort.update(any(TransactionDomain.class))).thenThrow(dbException);

        mockMvc.perform(put("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorMessage()));

        verify(updateTransactionPort, times(1)).update(any(TransactionDomain.class));
    }

    @Test
    void testUpdateTransactionUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(updateTransactionPort.update(any(TransactionDomain.class))).thenThrow(exception);

        mockMvc.perform(put("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransactionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR.getErrorMessage()));

        verify(updateTransactionPort, times(1)).update(any(TransactionDomain.class));
    }

    @Test
    void testDeleteTransactionSuccess() throws Exception {
        doNothing().when(deleteTransactionPort).delete(transactionId);

        mockMvc.perform(delete("/movimientos/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteTransactionPort, times(1)).delete(transactionId);
    }

    @Test
    void testDeleteTransactionNotFound() throws Exception {
        DataBaseException dbException = new DataBaseException(
                SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode(),
                "Transaction not found"
        );

        doThrow(dbException).when(deleteTransactionPort).delete(transactionId);

        mockMvc.perform(delete("/movimientos/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorMessage()));

        verify(deleteTransactionPort, times(1)).delete(transactionId);
    }

    @Test
    void testDeleteTransactionUnknownException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");

        doThrow(exception).when(deleteTransactionPort).delete(transactionId);

        mockMvc.perform(delete("/movimientos/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code_status").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorCode()))
                .andExpect(jsonPath("$.message").value(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR.getErrorMessage()));

        verify(deleteTransactionPort, times(1)).delete(transactionId);
    }
}
