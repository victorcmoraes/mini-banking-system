package dev.victorcmoraes.minibanking.application.service;

import dev.victorcmoraes.minibanking.application.port.in.TransferCommand;
import dev.victorcmoraes.minibanking.application.port.in.TransferReceipt;
import dev.victorcmoraes.minibanking.application.port.out.AccountRepositoryPort;
import dev.victorcmoraes.minibanking.application.port.out.TransactionRepositoryPort;
import dev.victorcmoraes.minibanking.domain.Account;
import dev.victorcmoraes.minibanking.domain.BankTransaction;
import dev.victorcmoraes.minibanking.domain.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferMoneyServiceTest {

    @Test
    void transfersMoneyAndStoresTransaction() {
        InMemoryBankingRepository repository = new InMemoryBankingRepository();
        repository.accounts.put(1001L, new Account(1001L, "Victor", new BigDecimal("100.00")));
        repository.accounts.put(1002L, new Account(1002L, "Destino", new BigDecimal("20.00")));

        TransferMoneyService service = new TransferMoneyService(repository, repository);

        TransferReceipt receipt = service.transfer(new TransferCommand(
                1001L,
                1002L,
                new BigDecimal("35.50"),
                "idem-1"
        ));

        assertThat(receipt.replayed()).isFalse();
        assertThat(repository.accounts.get(1001L).balance()).isEqualByComparingTo("64.50");
        assertThat(repository.accounts.get(1002L).balance()).isEqualByComparingTo("55.50");
        assertThat(repository.transactions).hasSize(1);
    }

    @Test
    void returnsSameReceiptWhenIdempotencyKeyIsRepeatedWithSamePayload() {
        InMemoryBankingRepository repository = new InMemoryBankingRepository();
        repository.accounts.put(1001L, new Account(1001L, "Victor", new BigDecimal("100.00")));
        repository.accounts.put(1002L, new Account(1002L, "Destino", new BigDecimal("20.00")));

        TransferMoneyService service = new TransferMoneyService(repository, repository);
        TransferCommand command = new TransferCommand(1001L, 1002L, new BigDecimal("10.00"), "idem-repeat");

        TransferReceipt first = service.transfer(command);
        TransferReceipt second = service.transfer(command);

        assertThat(first.transactionId()).isEqualTo(second.transactionId());
        assertThat(second.replayed()).isTrue();
        assertThat(repository.accounts.get(1001L).balance()).isEqualByComparingTo("90.00");
        assertThat(repository.accounts.get(1002L).balance()).isEqualByComparingTo("30.00");
        assertThat(repository.transactions).hasSize(1);
    }

    @Test
    void rejectsTransferWhenBalanceIsInsufficient() {
        InMemoryBankingRepository repository = new InMemoryBankingRepository();
        repository.accounts.put(1001L, new Account(1001L, "Victor", new BigDecimal("5.00")));
        repository.accounts.put(1002L, new Account(1002L, "Destino", new BigDecimal("20.00")));

        TransferMoneyService service = new TransferMoneyService(repository, repository);

        assertThatThrownBy(() -> service.transfer(new TransferCommand(
                1001L,
                1002L,
                new BigDecimal("15.00"),
                "idem-2"
        ))).isInstanceOf(InsufficientFundsException.class);
    }

    private static class InMemoryBankingRepository implements AccountRepositoryPort, TransactionRepositoryPort {
        private final Map<Long, Account> accounts = new HashMap<>();
        private final List<BankTransaction> transactions = new ArrayList<>();
        private long nextTransactionId = 1L;

        @Override
        public Optional<Account> findById(Long accountId) {
            return Optional.ofNullable(accounts.get(accountId));
        }

        @Override
        public Optional<Account> findByIdForUpdate(Long accountId) {
            return Optional.ofNullable(accounts.get(accountId));
        }

        @Override
        public Account save(Account account) {
            accounts.put(account.id(), account);
            return account;
        }

        @Override
        public Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey) {
            return transactions.stream()
                    .filter(transaction -> transaction.idempotencyKey().equals(idempotencyKey))
                    .findFirst();
        }

        @Override
        public List<BankTransaction> findByAccountId(Long accountId) {
            return transactions.stream()
                    .filter(transaction -> transaction.sourceAccountId().equals(accountId)
                            || transaction.targetAccountId().equals(accountId))
                    .toList();
        }

        @Override
        public BankTransaction save(BankTransaction transaction) {
            BankTransaction saved = new BankTransaction(
                    nextTransactionId++,
                    transaction.sourceAccountId(),
                    transaction.targetAccountId(),
                    transaction.amount(),
                    transaction.idempotencyKey(),
                    transaction.status(),
                    transaction.failureReason(),
                    transaction.createdAt()
            );
            transactions.add(saved);
            return saved;
        }
    }
}
