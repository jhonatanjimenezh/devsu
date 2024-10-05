package com.devsu.ws_account.adapter.controller;

import com.devsu.ws_account.adapter.controller.models.CreateAccountRequest;
import com.devsu.ws_account.adapter.controller.models.AccountResponse;
import com.devsu.ws_account.adapter.controller.models.UpdateAccountRequest;
import com.devsu.ws_account.application.port.in.account.*;
import com.devsu.ws_account.config.exception.CustomException;
import com.devsu.ws_account.config.exception.DataBaseException;
import com.devsu.ws_account.config.exception.ErrorResponse;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
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
@RequestMapping("/cuentas")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {})
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final CreateAccountPort createAccountPort;
    private final GetAllAccountsPort getAllAccountsPort;
    private final GetAccountByIdPort getAccountByIdPort;
    private final UpdateAccountPort updateAccountPort;
    private final DeleteAccountPort deleteAccountPort;

    public AccountController(CreateAccountPort createAccountPort, GetAllAccountsPort getAllAccountsPort,
                             GetAccountByIdPort getAccountByIdPort, UpdateAccountPort updateAccountPort,
                             DeleteAccountPort deleteAccountPort) {
        this.createAccountPort = createAccountPort;
        this.getAllAccountsPort = getAllAccountsPort;
        this.getAccountByIdPort = getAccountByIdPort;
        this.updateAccountPort = updateAccountPort;
        this.deleteAccountPort = deleteAccountPort;
    }

    @PostMapping
    public ResponseEntity<Object> createAccount(@Valid @RequestBody CreateAccountRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed for CreateAccountRequest: {}", bindingResult.getFieldErrors());
            return handleValidationError(bindingResult);
        }

        try {
            logger.info("Creating account for client ID: {}", request.getClientId());
            AccountDomain createdAccount = createAccountPort.create(request.toDomain());
            logger.info("Account created successfully with ID: {}", createdAccount.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.of(createdAccount, HttpStatus.CREATED));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_CREATE_ERROR, ex);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        try {
            logger.info("Fetching all accounts");
            List<AccountDomain> accounts = getAllAccountsPort.getAll();
            logger.info("Successfully retrieved all accounts, total: {}", accounts.size());
            return ResponseEntity.ok(AccountResponse.of(accounts, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_FIND_ALL_ERROR, ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable UUID id) {
        try {
            logger.info("Fetching account by ID: {}", id);
            AccountDomain account = getAccountByIdPort.getById(id);
            logger.info("Successfully retrieved account with ID: {}", id);
            return ResponseEntity.ok(AccountResponse.of(account, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_FIND_BY_ID_ERROR, ex);
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateAccount(@Valid @RequestBody UpdateAccountRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed for UpdateAccountRequest: {}", bindingResult.getFieldErrors());
            return handleValidationError(bindingResult);
        }

        try {
            logger.info("Updating account with ID: {}", request.getId());
            AccountDomain updatedAccount = updateAccountPort.update(request.toDomain());
            logger.info("Account updated successfully with ID: {}", updatedAccount.getId());
            return ResponseEntity.ok(AccountResponse.of(updatedAccount, HttpStatus.OK));
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_UPDATE_ERROR, ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable UUID id) {
        try {
            logger.info("Received request to delete account with ID: {}", id);
            deleteAccountPort.delete(id);
            logger.info("Account deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return handleError(SPError.ACCOUNT_CONTROLLER_DELETE_ERROR, ex);
        }
    }


    private ResponseEntity<Object> handleValidationError(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(AccountResponse.badRequest(bindingResult));
    }

    private ResponseEntity<Object> handleError(SPError error, Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(false, error.getErrorCode(), error.getErrorMessage(), ex.getCause());
        logger.error("Error occurred: {}", errorResponse, ex);

        HttpStatus status = ex instanceof DataBaseException || ex instanceof CustomException
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(errorResponse);
    }
}
