package dev.victorcmoraes.minibanking.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record BankTransaction(
        Long id,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String idempotencyKey,
        TransactionStatus status,
        String failureReason,
        Instant createdAt
) {
    public static BankTransaction completed(Long sourceAccountId, Long targetAccountId, BigDecimal amount, String idempotencyKey) {
        return new BankTransaction(
                null,
                sourceAccountId,
                targetAccountId,
                amount,
                idempotencyKey,
                TransactionStatus.COMPLETED,
                null,
                Instant.now()
        );
    }

    public boolean matches(TransferCommandSnapshot snapshot) {
        return sourceAccountId.equals(snapshot.sourceAccountId())
                && targetAccountId.equals(snapshot.targetAccountId())
                && amount.compareTo(snapshot.amount()) == 0;
    }
}
