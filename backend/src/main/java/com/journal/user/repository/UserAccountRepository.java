package com.journal.user.repository;

import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserAccount> findByRole(RoleType role);
}
