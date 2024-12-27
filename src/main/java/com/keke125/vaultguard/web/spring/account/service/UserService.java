package com.keke125.vaultguard.web.spring.account.service;

import com.keke125.vaultguard.web.spring.account.entity.VerificationCode;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.entity.VerificationType;
import com.keke125.vaultguard.web.spring.account.repository.VerificationCodeRepository;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;

    @Getter
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
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

    public void createOrUpdateVerificationCode(User user, String token, Optional<VerificationCode> verificationCode, VerificationType type) {
        Date now = new Date();
        VerificationCode newVerificationCode;
        newVerificationCode = verificationCode.orElseGet(VerificationCode::new);
        Date expiryDate = new Date(now.getTime() + VerificationCode.EXPIRATION * 1000);
        newVerificationCode.setUser(user);
        newVerificationCode.setToken(token);
        newVerificationCode.setExpiryDate(expiryDate);
        newVerificationCode.setValid(true);
        newVerificationCode.setVerificationType(type);
        verificationCodeRepository.save(newVerificationCode);
    }

    public boolean validateVerificationCode(User user, String token, VerificationType type) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByUserAndTokenAndIsValidAndVerificationType(user, token, true, type);
        verificationCode.ifPresent(code -> code.setValid(false));
        return verificationCode.map(code -> code.getExpiryDate().after(new Date())).orElse(false);
    }

    public Optional<VerificationCode> findVerificationCodeByUserAndVerificationType(User user, VerificationType type) {
        return verificationCodeRepository.findByUserAndVerificationType(user, type);
    }
}
