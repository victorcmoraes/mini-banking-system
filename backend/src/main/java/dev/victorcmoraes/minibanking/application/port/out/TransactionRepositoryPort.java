package dev.victorcmoraes.minibanking.application.port.out;

import dev.victorcmoraes.minibanking.domain.BankTransaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);

    List<BankTransaction> findByAccountId(Long accountId);

    BankTransaction save(BankTransaction transaction);
}
