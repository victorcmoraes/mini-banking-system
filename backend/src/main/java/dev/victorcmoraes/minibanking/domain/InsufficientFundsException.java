package dev.victorcmoraes.minibanking.domain;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super("Saldo insuficiente na conta " + accountId + ". Saldo=" + balance + ", valor=" + amount);
    }
}
