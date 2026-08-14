package dev.victorcmoraes.minibanking.application.port.in;

import dev.victorcmoraes.minibanking.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionView(
        Long id,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        TransactionStatus status,
        Instant createdAt
) {
}
