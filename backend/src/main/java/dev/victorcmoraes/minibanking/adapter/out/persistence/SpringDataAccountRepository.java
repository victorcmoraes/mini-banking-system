package dev.victorcmoraes.minibanking.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from AccountJpaEntity account where account.id = :accountId")
    Optional<AccountJpaEntity> findByIdForUpdate(@Param("accountId") Long accountId);
}
