package dev.victorcmoraes.minibanking.adapter.in.web;

import dev.victorcmoraes.minibanking.domain.AccountNotFoundException;
import dev.victorcmoraes.minibanking.domain.DuplicateIdempotencyKeyException;
import dev.victorcmoraes.minibanking.domain.InsufficientFundsException;
import dev.victorcmoraes.minibanking.domain.InvalidTransferException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, "NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<ApiError> handleConflict(DuplicateIdempotencyKeyException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, "IDEMPOTENCY_CONFLICT", exception.getMessage()));
    }

    @ExceptionHandler({InvalidTransferException.class, InsufficientFundsException.class})
    public ResponseEntity<ApiError> handleBusinessRule(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of(422, "BUSINESS_RULE", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "valor invalido" : error.getDefaultMessage(),
                        (left, right) -> left
                ));
        return ResponseEntity.badRequest()
                .body(ApiError.validation(400, "VALIDATION", "Payload invalido.", fields));
    }
}
