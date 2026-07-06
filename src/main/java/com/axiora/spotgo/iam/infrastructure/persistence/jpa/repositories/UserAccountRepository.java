package com.axiora.spotgo.iam.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.iam.domain.model.aggregates.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
