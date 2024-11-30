package com.keke125.vaultguard.web.spring.password.repository;

import com.keke125.vaultguard.web.spring.password.entity.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PasswordRepository extends JpaRepository<Password, String>, JpaSpecificationExecutor<Password> {
    List<Password> findAllByUserUid(String userUid);

    Optional<Password> findByUidAndUserUid(String uid, String userUid);

    Optional<Password> findByNameAndUsernameAndUserUid(String name, String username, String userUid);

    void deleteAllByUserUid(String userUid);
}
