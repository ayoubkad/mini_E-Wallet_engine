package com.ewallet.exception;

import com.ewallet.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(
            AccountNotFoundException ex
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                AccountNotFoundException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<Map<String, String>> handleCurrencyMismatch(
            CurrencyMismatchException ex
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_CONTENT,
                CurrencyMismatchException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleDailyLimit(
            DailyLimitExceededException ex
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_CONTENT,
                DailyLimitExceededException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientFunds(
            InsufficientFundsException ex
    ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_CONTENT,
                InsufficientFundsException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, String>> handleIdempotencyConflict(
            IdempotencyConflictException ex
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                IdempotencyConflictException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidAccountStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAccountState(
            InvalidAccountStateException ex
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                InvalidAccountStateException.ERROR_CODE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(
                        error ->
                                errors.putIfAbsent(
                                        error.getField(),
                                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "message invalide"
                                )
                );

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                errorResponse
        );
    }


    private ResponseEntity<Map<String, String>> buildError(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "code", code,
                        "message", message
                ));
    }


}