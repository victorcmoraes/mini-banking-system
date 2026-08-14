package dev.victorcmoraes.minibanking.application.port.in;

import dev.victorcmoraes.minibanking.domain.TransferCommandSnapshot;

import java.math.BigDecimal;

public record TransferCommand(
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String idempotencyKey
) {
    public TransferCommandSnapshot snapshot() {
        return new TransferCommandSnapshot(sourceAccountId, targetAccountId, amount);
    }
}
