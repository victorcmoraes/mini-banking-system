package dev.victorcmoraes.minibanking.adapter.out.persistence;

import dev.victorcmoraes.minibanking.application.port.out.AccountRepositoryPort;
import dev.victorcmoraes.minibanking.application.port.out.TransactionRepositoryPort;
import dev.victorcmoraes.minibanking.domain.Account;
import dev.victorcmoraes.minibanking.domain.AccountNotFoundException;
import dev.victorcmoraes.minibanking.domain.BankTransaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BankingPersistenceAdapter implements AccountRepositoryPort, TransactionRepositoryPort {
    private final SpringDataAccountRepository accountRepository;
    private final SpringDataTransactionRepository transactionRepository;

    public BankingPersistenceAdapter(
            SpringDataAccountRepository accountRepository,
            SpringDataTransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId).map(AccountJpaEntity::toDomain);
    }

    @Override
    public Optional<Account> findByIdForUpdate(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId).map(AccountJpaEntity::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = accountRepository.findById(account.id())
                .orElseThrow(() -> new AccountNotFoundException(account.id()));
        entity.updateFrom(account);
        return accountRepository.save(entity).toDomain();
    }

    @Override
    public Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey) {
        return transactionRepository.findByIdempotencyKey(idempotencyKey).map(TransactionJpaEntity::toDomain);
    }

    @Override
    public List<BankTransaction> findByAccountId(Long accountId) {
        return transactionRepository.findBySourceAccountIdOrTargetAccountId(accountId, accountId).stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public BankTransaction save(BankTransaction transaction) {
        return transactionRepository.save(TransactionJpaEntity.from(transaction)).toDomain();
    }
}
