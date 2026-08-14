package dev.victorcmoraes.minibanking.application.service;

import dev.victorcmoraes.minibanking.application.port.in.TransferCommand;
import dev.victorcmoraes.minibanking.application.port.in.TransferReceipt;
import dev.victorcmoraes.minibanking.application.port.in.TransferUseCase;
import dev.victorcmoraes.minibanking.application.port.out.AccountRepositoryPort;
import dev.victorcmoraes.minibanking.application.port.out.TransactionRepositoryPort;
import dev.victorcmoraes.minibanking.domain.Account;
import dev.victorcmoraes.minibanking.domain.AccountNotFoundException;
import dev.victorcmoraes.minibanking.domain.BankTransaction;
import dev.victorcmoraes.minibanking.domain.DuplicateIdempotencyKeyException;
import dev.victorcmoraes.minibanking.domain.InvalidTransferException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class TransferMoneyService implements TransferUseCase {
    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;

    public TransferMoneyService(AccountRepositoryPort accountRepository, TransactionRepositoryPort transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TransferReceipt transfer(TransferCommand command) {
        validate(command);

        return transactionRepository.findByIdempotencyKey(command.idempotencyKey())
                .map(existing -> receiptForRepeatedRequest(existing, command))
                .orElseGet(() -> executeNewTransfer(command));
    }

    private TransferReceipt executeNewTransfer(TransferCommand command) {
        List<Account> lockedAccounts = lockAccountsInStableOrder(command.sourceAccountId(), command.targetAccountId());
        Account source = accountById(lockedAccounts, command.sourceAccountId());
        Account target = accountById(lockedAccounts, command.targetAccountId());

        source.debit(command.amount());
        target.credit(command.amount());

        accountRepository.save(source);
        accountRepository.save(target);

        BankTransaction saved = transactionRepository.save(BankTransaction.completed(
                command.sourceAccountId(),
                command.targetAccountId(),
                command.amount(),
                command.idempotencyKey()
        ));

        return TransferReceipt.from(saved, false);
    }

    private TransferReceipt receiptForRepeatedRequest(BankTransaction existing, TransferCommand command) {
        if (!existing.matches(command.snapshot())) {
            throw new DuplicateIdempotencyKeyException(command.idempotencyKey());
        }
        return TransferReceipt.from(existing, true);
    }

    private List<Account> lockAccountsInStableOrder(Long sourceAccountId, Long targetAccountId) {
        return List.of(sourceAccountId, targetAccountId).stream()
                .sorted(Comparator.naturalOrder())
                .map(accountId -> accountRepository.findByIdForUpdate(accountId)
                        .orElseThrow(() -> new AccountNotFoundException(accountId)))
                .toList();
    }

    private Account accountById(List<Account> accounts, Long accountId) {
        return accounts.stream()
                .filter(account -> account.id().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void validate(TransferCommand command) {
        if (command == null) {
            throw new InvalidTransferException("Comando de transferencia e obrigatorio.");
        }
        if (command.sourceAccountId() == null || command.targetAccountId() == null) {
            throw new InvalidTransferException("Conta de origem e destino sao obrigatorias.");
        }
        if (command.sourceAccountId().equals(command.targetAccountId())) {
            throw new InvalidTransferException("Conta de origem e destino devem ser diferentes.");
        }
        if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("O valor da transferencia deve ser maior que zero.");
        }
        if (command.amount().scale() > 2) {
            throw new InvalidTransferException("O valor da transferencia deve ter no maximo duas casas decimais.");
        }
        if (command.idempotencyKey() == null || command.idempotencyKey().isBlank()) {
            throw new InvalidTransferException("Header Idempotency-Key e obrigatorio.");
        }
    }
}
