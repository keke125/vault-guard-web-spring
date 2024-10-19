package com.keke125.vaultguard.web.spring.account.service;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    @Getter
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public void store(User user) {
        repository.save(user);
    }

    public boolean isUsernameNonExist(String username) {
        return repository.findByUsername(username).isEmpty();
    }

    public boolean isEmailNonExist(String email) {
        return repository.findAllByEmail(email).isEmpty();
    }

    public User findByUsername(String userName) {
        return repository.findByUsername(userName).orElse(null);
    }

}
