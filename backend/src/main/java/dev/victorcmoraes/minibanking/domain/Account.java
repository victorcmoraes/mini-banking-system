package dev.victorcmoraes.minibanking.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Account {
    private final Long id;
    private final String ownerName;
    private BigDecimal balance;

    public Account(Long id, String ownerName, BigDecimal balance) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.ownerName = requireText(ownerName, "ownerName is required");
        this.balance = normalize(balance);
    }

    public Long id() {
        return id;
    }

    public String ownerName() {
        return ownerName;
    }

    public BigDecimal balance() {
        return balance;
    }

    public void debit(BigDecimal amount) {
        BigDecimal normalizedAmount = requirePositiveAmount(amount);
        if (balance.compareTo(normalizedAmount) < 0) {
            throw new InsufficientFundsException(id, balance, normalizedAmount);
        }
        balance = balance.subtract(normalizedAmount).setScale(2, RoundingMode.HALF_EVEN);
    }

    public void credit(BigDecimal amount) {
        BigDecimal normalizedAmount = requirePositiveAmount(amount);
        balance = balance.add(normalizedAmount).setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal requirePositiveAmount(BigDecimal amount) {
        BigDecimal normalized = normalize(amount);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("O valor da transferencia deve ser maior que zero.");
        }
        return normalized;
    }

    private static BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            throw new InvalidTransferException("Valor monetario e obrigatorio.");
        }
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
