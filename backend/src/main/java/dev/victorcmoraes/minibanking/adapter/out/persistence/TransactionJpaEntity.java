package dev.victorcmoraes.minibanking.adapter.out.persistence;

import dev.victorcmoraes.minibanking.domain.BankTransaction;
import dev.victorcmoraes.minibanking.domain.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bank_transactions")
public class TransactionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TransactionJpaEntity() {
    }

    private TransactionJpaEntity(
            Long id,
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal amount,
            String idempotencyKey,
            TransactionStatus status,
            String failureReason,
            Instant createdAt
    ) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public static TransactionJpaEntity from(BankTransaction transaction) {
        return new TransactionJpaEntity(
                transaction.id(),
                transaction.sourceAccountId(),
                transaction.targetAccountId(),
                transaction.amount(),
                transaction.idempotencyKey(),
                transaction.status(),
                transaction.failureReason(),
                transaction.createdAt()
        );
    }

    public BankTransaction toDomain() {
        return new BankTransaction(
                id,
                sourceAccountId,
                targetAccountId,
                amount,
                idempotencyKey,
                status,
                failureReason,
                createdAt
        );
    }
}
