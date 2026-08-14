package dev.victorcmoraes.minibanking.domain;

public class DuplicateIdempotencyKeyException extends RuntimeException {

    public DuplicateIdempotencyKeyException(String idempotencyKey) {
        super("Chave de idempotencia reutilizada com dados diferentes: " + idempotencyKey);
    }
}
