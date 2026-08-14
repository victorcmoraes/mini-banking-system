package dev.victorcmoraes.minibanking.adapter.in.web;

import dev.victorcmoraes.minibanking.application.port.in.AccountQueryUseCase;
import dev.victorcmoraes.minibanking.application.port.in.AccountView;
import dev.victorcmoraes.minibanking.application.port.in.TransactionView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
public class AccountController {
    private final AccountQueryUseCase accountQueryUseCase;

    public AccountController(AccountQueryUseCase accountQueryUseCase) {
        this.accountQueryUseCase = accountQueryUseCase;
    }

    @GetMapping("/api/v1/accounts/{accountId}")
    public AccountResponse getAccount(@PathVariable Long accountId) {
        AccountView account = accountQueryUseCase.getAccount(accountId);
        return new AccountResponse(account.id(), account.ownerName(), account.balance());
    }

    @GetMapping("/api/v1/accounts/{accountId}/statement")
    public StatementResponse getStatement(@PathVariable Long accountId) {
        List<TransactionResponse> transactions = accountQueryUseCase.getStatement(accountId).stream()
                .map(TransactionResponse::from)
                .toList();
        return new StatementResponse(accountId, transactions);
    }

    public record AccountResponse(Long id, String ownerName, BigDecimal balance) {
    }

    public record StatementResponse(Long accountId, List<TransactionResponse> transactions) {
    }

    public record TransactionResponse(
            Long id,
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal amount,
            String status,
            Instant createdAt
    ) {
        static TransactionResponse from(TransactionView transaction) {
            return new TransactionResponse(
                    transaction.id(),
                    transaction.sourceAccountId(),
                    transaction.targetAccountId(),
                    transaction.amount(),
                    transaction.status().name(),
                    transaction.createdAt()
            );
        }
    }
}
