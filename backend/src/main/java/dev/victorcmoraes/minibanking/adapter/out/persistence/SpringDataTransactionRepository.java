package dev.victorcmoraes.minibanking.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, Long> {
    Optional<TransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);

    List<TransactionJpaEntity> findBySourceAccountIdOrTargetAccountId(Long sourceAccountId, Long targetAccountId);
}
