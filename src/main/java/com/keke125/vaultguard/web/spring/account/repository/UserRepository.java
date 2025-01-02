package com.keke125.vaultguard.web.spring.account.repository;

import com.keke125.vaultguard.web.spring.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUid(String userUid);

    List<User> findAllByEmail(String email);

    Optional<User> findByEmail(String email);
}
