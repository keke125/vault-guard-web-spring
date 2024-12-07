package com.keke125.vaultguard.web.spring.account.repository;

import com.keke125.vaultguard.web.spring.account.entity.PasswordResetToken;
import com.keke125.vaultguard.web.spring.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String>, JpaSpecificationExecutor<PasswordResetToken> {
    Optional<PasswordResetToken> findByUserAndTokenAndIsValid(User user, String token, boolean isValid);

    Optional<PasswordResetToken> findByUser(User user);
}
