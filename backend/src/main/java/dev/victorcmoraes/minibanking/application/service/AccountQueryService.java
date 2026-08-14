package dev.victorcmoraes.minibanking.application.service;

import dev.victorcmoraes.minibanking.application.port.in.AccountQueryUseCase;
import dev.victorcmoraes.minibanking.application.port.in.AccountView;
import dev.victorcmoraes.minibanking.application.port.in.TransactionView;
import dev.victorcmoraes.minibanking.application.port.out.AccountRepositoryPort;
import dev.victorcmoraes.minibanking.application.port.out.TransactionRepositoryPort;
import dev.victorcmoraes.minibanking.domain.Account;
import dev.victorcmoraes.minibanking.domain.AccountNotFoundException;
import dev.victorcmoraes.minibanking.domain.BankTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AccountQueryService implements AccountQueryUseCase {
    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;

    public AccountQueryService(AccountRepositoryPort accountRepository, TransactionRepositoryPort transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountView getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        return new AccountView(account.id(), account.ownerName(), account.balance());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionView> getStatement(Long accountId) {
        accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));

        return transactionRepository.findByAccountId(accountId).stream()
                .sorted(Comparator.comparing(BankTransaction::createdAt).reversed())
                .map(transaction -> new TransactionView(
                        transaction.id(),
                        transaction.sourceAccountId(),
                        transaction.targetAccountId(),
                        transaction.amount(),
                        transaction.status(),
                        transaction.createdAt()
                ))
                .toList();
    }
}
