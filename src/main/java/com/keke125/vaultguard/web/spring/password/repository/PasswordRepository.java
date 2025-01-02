package com.keke125.vaultguard.web.spring.password.repository;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.password.entity.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PasswordRepository extends JpaRepository<Password, String>, JpaSpecificationExecutor<Password> {
    List<Password> findAllByUser(User user);

    Optional<Password> findByUidAndUser(String uid, User user);

    Optional<Password> findByNameAndUsernameAndUser(String name, String username, User user);

    void deleteAllByUser(User user);
}
