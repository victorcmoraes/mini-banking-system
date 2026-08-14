package dev.victorcmoraes.minibanking.domain;

public class InvalidTransferException extends RuntimeException {

    public InvalidTransferException(String message) {
        super(message);
    }
}
