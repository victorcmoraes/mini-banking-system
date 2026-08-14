package dev.victorcmoraes.minibanking.domain;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long accountId) {
        super("Conta nao encontrada: " + accountId);
    }
}
