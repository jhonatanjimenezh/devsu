package com.devsu.ws_account.adapter.controller;

import com.devsu.ws_account.adapter.controller.models.CreateTransactionRequest;
import com.devsu.ws_account.adapter.controller.models.TransactionResponse;
import com.devsu.ws_account.adapter.controller.models.UpdateTransactionRequest;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.ErrorResponse;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.application.port.in.transaction.*;
import com.devsu.ws_account.domain.TransactionDomain;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {})
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final CreateTransactionPort createTransactionPort;
    private final GetAllTransactionsPort getAllTransactionsPort;
    private final GetTransactionByIdPort getTransactionByIdPort;
    private final UpdateTransactionPort updateTransactionPort;
    private final DeleteTransactionPort deleteTransactionPort;

    public TransactionController(CreateTransactionPort createTransactionPort, GetAllTransactionsPort getAllTransactionsPort,
                                 GetTransactionByIdPort getTransactionByIdPort, UpdateTransactionPort updateTransactionPort,
                                 DeleteTransactionPort deleteTransactionPort) {
        this.createTransactionPort = createTransactionPort;
        this.getAllTransactionsPort = getAllTransactionsPort;
        this.getTransactionByIdPort = getTransactionByIdPort;
        this.updateTransactionPort = updateTransactionPort;
        this.deleteTransactionPort = deleteTransactionPort;
    }

    @PostMapping
    public ResponseEntity<Object> createTransaction(@Valid @RequestBody CreateTransactionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed for CreateTransactionRequest: {}", bindingResult.getFieldErrors());
            return handleValidationErrors(bindingResult);
        }

        try {
            logger.info("Creating transaction for account ID: {}", request.getAccountId());
            TransactionDomain createdTransaction = createTransactionPort.create(request.toDomain());
            logger.info("Transaction created successfully with ID: {}", createdTransaction.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.of(createdTransaction, HttpStatus.CREATED));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR, ex);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllTransactions() {
        try {
            logger.info("Fetching all transactions");
            List<TransactionDomain> transactions = getAllTransactionsPort.getAll();
            logger.info("Successfully retrieved all transactions, total: {}", transactions.size());
            return ResponseEntity.ok(TransactionResponse.of(transactions, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR, ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable UUID id) {
        try {
            logger.info("Fetching transaction by ID: {}", id);
            TransactionDomain transaction = getTransactionByIdPort.getById(id);
            logger.info("Successfully retrieved transaction with ID: {}", id);
            return ResponseEntity.ok(TransactionResponse.of(transaction, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR, ex);
        }
    }

    @PutMapping()
    public ResponseEntity<Object> updateTransaction(@Valid @RequestBody UpdateTransactionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed for UpdateTransactionRequest: {}", bindingResult.getFieldErrors());
            return handleValidationErrors(bindingResult);
        }

        try {
            logger.info("Updating transaction with ID: {}", request.getId());
            TransactionDomain updatedTransaction = updateTransactionPort.update(request.toDomain());
            logger.info("Transaction updated successfully with ID: {}", updatedTransaction.getId());
            return ResponseEntity.ok(TransactionResponse.of(updatedTransaction, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR, ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable UUID id) {
        try {
            logger.info("Received request to delete transaction with ID: {}", id);
            deleteTransactionPort.delete(id);
            logger.info("Transaction deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR, ex);
        }
    }

    private ResponseEntity<Object> handleValidationErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(TransactionResponse.badRequest(bindingResult));
    }

    private ResponseEntity<Object> handleError(SPError error, Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(false, error.getErrorCode(), error.getErrorMessage(), ex.getCause());
        logger.error("Error occurred: {}", errorResponse, ex);

        HttpStatus status = (ex instanceof DataBaseException || ex instanceof CustomException)
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(errorResponse);
    }
}
