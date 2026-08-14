package dev.victorcmoraes.minibanking.application.port.out;

import dev.victorcmoraes.minibanking.domain.Account;

import java.util.Optional;

public interface AccountRepositoryPort {
    Optional<Account> findById(Long accountId);

    Optional<Account> findByIdForUpdate(Long accountId);

    Account save(Account account);
}
