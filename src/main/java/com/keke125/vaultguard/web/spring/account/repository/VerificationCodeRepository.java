package com.keke125.vaultguard.web.spring.account.repository;

import com.keke125.vaultguard.web.spring.account.entity.VerificationCode;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.entity.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String>, JpaSpecificationExecutor<VerificationCode> {
    Optional<VerificationCode> findByUserAndCodeAndValidAndVerificationType(User user, String code, boolean valid,
                                                                            VerificationType verificationType);

    Optional<VerificationCode> findByUserAndVerificationType(User user, VerificationType verificationType);
}
