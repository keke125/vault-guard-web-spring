package com.keke125.vaultguard.web.spring.account.service;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    @Getter
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void update(User entity) {
        repository.save(entity);
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

    public Optional<User> findByUsername(String userName) {
        return repository.findByUsername(userName);
    }

    public boolean checkMainPassword(String username, String password) {
        Optional<User> user = repository.findByUsername(username);
        return user.filter(value -> passwordEncoder.matches(password, value.getHashedPassword())).isPresent();
    }

}
