package dev.victorcmoraes.minibanking.adapter.out.persistence;

import dev.victorcmoraes.minibanking.domain.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {
    @Id
    private Long id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(nullable = false)
    private Long version;

    protected AccountJpaEntity() {
    }

    public Account toDomain() {
        return new Account(id, ownerName, balance);
    }

    public void updateFrom(Account account) {
        this.balance = account.balance();
    }
}
