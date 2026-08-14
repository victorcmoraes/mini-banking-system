package dev.victorcmoraes.minibanking.domain;

import java.math.BigDecimal;

public record TransferCommandSnapshot(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
}
