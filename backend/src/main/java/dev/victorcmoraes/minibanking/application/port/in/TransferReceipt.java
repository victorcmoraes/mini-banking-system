package dev.victorcmoraes.minibanking.application.port.in;

import dev.victorcmoraes.minibanking.domain.BankTransaction;
import dev.victorcmoraes.minibanking.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferReceipt(
        Long transactionId,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        TransactionStatus status,
        Instant createdAt,
        boolean replayed
) {
    public static TransferReceipt from(BankTransaction transaction, boolean replayed) {
        return new TransferReceipt(
                transaction.id(),
                transaction.sourceAccountId(),
                transaction.targetAccountId(),
                transaction.amount(),
                transaction.status(),
                transaction.createdAt(),
                replayed
        );
    }
}
