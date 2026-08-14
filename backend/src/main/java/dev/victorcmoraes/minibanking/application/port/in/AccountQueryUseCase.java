package dev.victorcmoraes.minibanking.application.port.in;

import java.util.List;

public interface AccountQueryUseCase {
    AccountView getAccount(Long accountId);

    List<TransactionView> getStatement(Long accountId);
}
