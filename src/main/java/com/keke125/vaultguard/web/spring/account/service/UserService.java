package com.keke125.vaultguard.web.spring.account.service;

import com.keke125.vaultguard.web.spring.account.entity.VerificationCode;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.entity.VerificationType;
import com.keke125.vaultguard.web.spring.account.repository.VerificationCodeRepository;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return findByUsername(username).isEmpty();
    }

    public boolean isEmailExist(String email) {
        return !userRepository.findAllByEmail(email).isEmpty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserUid(String userUid) {
        return userRepository.findByUid(userUid);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isMainPasswordMismatch(String userUid, String password) {
        Optional<User> user = findByUserUid(userUid);
        return user.filter(value -> passwordEncoder.matches(password, value.getHashedPassword())).isEmpty();
    }

    public void createOrUpdateVerificationCode(User user, String code, Optional<VerificationCode> verificationCode, VerificationType type) {
        VerificationCode newVerificationCode = getVerificationCode(user, code, verificationCode, type);
        verificationCodeRepository.save(newVerificationCode);
    }

    public void createOrUpdateVerificationCode(User user, String code, Optional<VerificationCode> verificationCode, VerificationType type, String email) {
        VerificationCode newVerificationCode = getVerificationCode(user, code, verificationCode, type);
        newVerificationCode.setEmail(email);
        verificationCodeRepository.save(newVerificationCode);
    }

    private static VerificationCode getVerificationCode(User user, String code, Optional<VerificationCode> verificationCode, VerificationType type) {
        VerificationCode newVerificationCode;
        newVerificationCode = verificationCode.orElseGet(VerificationCode::new);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusMinutes(VerificationCode.VERIFICATION_CODE_EXPIRATION);
        newVerificationCode.setUser(user);
        newVerificationCode.setCode(code);
        newVerificationCode.setExpiryDate(expiryDate);
        newVerificationCode.setValid(true);
        newVerificationCode.setVerificationType(type);
        return newVerificationCode;
    }

    public boolean validateVerificationCode(User user, String token, VerificationType type) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByUserAndCodeAndValidAndVerificationType(user, token, true, type);
        verificationCode.ifPresent(code -> code.setValid(false));
        return verificationCode.map(code -> code.getExpiryDate().isAfter(LocalDateTime.now())).orElse(false);
    }

    public boolean validateVerificationCode(User user, String token, VerificationType type, String email) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByUserAndCodeAndValidAndVerificationType(user, token, true, type);
        verificationCode.ifPresent(code -> code.setValid(false));
        return verificationCode.filter(code -> email.equals(code.getEmail())).map(code -> code.getExpiryDate().isAfter(LocalDateTime.now())).orElse(false);
    }

    public Optional<VerificationCode> findVerificationCodeByUserAndVerificationType(User user, VerificationType type) {
        return verificationCodeRepository.findByUserAndVerificationType(user, type);
    }
}
