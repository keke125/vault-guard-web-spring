package com.keke125.vaultguard.web.spring.account.service;

import com.keke125.vaultguard.web.spring.account.entity.PasswordResetToken;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.repository.PasswordResetTokenRepository;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Getter
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void update(User entity) {
        userRepository.save(entity);
    }

    public void store(User user) {
        userRepository.save(user);
    }

    public boolean isUsernameNonExist(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public boolean isEmailNonExist(String email) {
        return userRepository.findAllByEmail(email).isEmpty();
    }

    public Optional<User> findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkMainPassword(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(value -> passwordEncoder.matches(password, value.getHashedPassword())).isPresent();
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        Date now = new Date();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        Date expiryDate = new Date(now.getTime() + PasswordResetToken.EXPIRATION * 1000);
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(expiryDate);
        passwordResetToken.setValid(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public void updatePasswordResetTokenForUser(User user, String token, PasswordResetToken passwordResetToken) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + PasswordResetToken.EXPIRATION * 1000);
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(expiryDate);
        passwordResetToken.setValid(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public boolean validatePasswordResetToken(User user, String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByUserAndTokenAndIsValid(user, token, true);
        passwordResetToken.ifPresent(resetToken -> resetToken.setValid(false));
        return passwordResetToken.map(resetToken -> resetToken.getExpiryDate().after(new Date())).orElse(false);
    }

    public Optional<PasswordResetToken> findPasswordResetTokenByUser(User user) {
        return passwordResetTokenRepository.findByUser(user);
    }
}
