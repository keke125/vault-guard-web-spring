package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.VerificationCode;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.entity.VerificationType;
import com.keke125.vaultguard.web.spring.account.request.ActivateAccountRequest;
import com.keke125.vaultguard.web.spring.account.request.ChangeEmailRequest;
import com.keke125.vaultguard.web.spring.account.request.ResetPasswordRequest;
import com.keke125.vaultguard.web.spring.account.request.UpdateUserRequest;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import com.keke125.vaultguard.web.spring.mail.service.MailService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.*;
import static com.keke125.vaultguard.web.spring.password.ResponseMessage.errorMainPasswordResponse;

@RestController
@RequestMapping(value = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    private final MailService mailService;

    private final UserIdentity userIdentity;

    public UserController(UserService userService, UserIdentity userIdentity, MailService mailService) {
        this.userService = userService;
        this.userIdentity = userIdentity;
        this.mailService = mailService;
    }

    @GetMapping("/user")
    ResponseEntity<User> getUserProfileByJWT() {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/email")
    ResponseEntity<String> getEmailByJWT() {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        return ResponseEntity.ok(user.get().getEmail());
    }

    @GetMapping("/username")
    ResponseEntity<String> getUsernameByJWT() {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        return ResponseEntity.ok(user.get().getUsername());
    }

    @PatchMapping("/user")
    public ResponseEntity<Map<String, String>> updateUser(@RequestParam String type, @Valid @RequestBody UpdateUserRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }

        if (request.getOldPassword() == null) {
            return ResponseEntity.badRequest().body(emptyPasswordResponse);
        }

        if (userService.isMainPasswordMismatch(user.get().getUid(), request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMainPasswordResponse);
        }

        if (Objects.equals(type, "password")) {
            ResponseEntity<Map<String, String>> checkMainPasswordResponse = checkMainPassword(request.getNewPassword());
            if (checkMainPasswordResponse != null) return checkMainPasswordResponse;
            user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getNewPassword()));
        } else if (Objects.equals(type, "email")) {
            if (request.getNewEmail() == null) {
                return ResponseEntity.badRequest().body(emptyEmailResponse);
            }
            if (userService.isEmailExist(request.getNewEmail()) && !Objects.equals(request.getNewEmail(), user.get().getEmail())) {
                return ResponseEntity.badRequest().body(emailDuplicatedResponse);
            }
            if (!userService.validateVerificationCode(user.get(), request.getVerificationCode(), VerificationType.CHANGE_EMAIL, request.getNewEmail())) {
                return ResponseEntity.badRequest().body(failedFinishedChangeEmailResponse);
            }
            user.get().setEmail(request.getNewEmail());
        } else {
            return ResponseEntity.badRequest().body(disallowedUpdateUserTypeResponse);
        }
        userService.update(user.get());
        return ResponseEntity.ok(successUpdateResponse);
    }

    @PostMapping("/reset-password")
    ResponseEntity<Map<String, String>> resetPasswordByEmail(@Valid @RequestBody ResetPasswordRequest request) {
        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (!user.get().isEnabled()) {
            throw new DisabledException(userDisabledMessage);
        }
        if (request.getVerificationCode() != null) {
            if (userService.validateVerificationCode(user.get(), request.getVerificationCode(), VerificationType.RESET_PASSWORD)) {
                ResponseEntity<Map<String, String>> checkMainPasswordResponse = checkMainPassword(request.getNewPassword());
                if (checkMainPasswordResponse != null) return checkMainPasswordResponse;
                user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getNewPassword()));
                userService.update(user.get());
                return ResponseEntity.ok(successFinishedResetResponse);
            } else {
                return ResponseEntity.badRequest().body(failedFinishedResetResponse);
            }
        } else {
            ResponseEntity<Map<String, String>> failedSendVerificationCodeResponse = checkSendVerificationCodePeriod(user.get());
            if (failedSendVerificationCodeResponse != null) return failedSendVerificationCodeResponse;
            updateUserVerificationCodePeriod(user.get());
            Optional<VerificationCode> verificationCode = userService.findVerificationCodeByUserAndVerificationType(user.get(), VerificationType.RESET_PASSWORD);
            String token = UUID.randomUUID().toString();
            userService.createOrUpdateVerificationCode(user.get(), token, verificationCode, VerificationType.RESET_PASSWORD);
            mailService.sendMailResetPassword(user.get(), token);
            return ResponseEntity.ok(successSendResetPasswordResponse);
        }
    }

    @PostMapping("/activate-account")
    public ResponseEntity<Map<String, String>> activateAccountByEmail(@Valid @RequestBody ActivateAccountRequest request) {
        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (user.get().isEnabled()) {
            return ResponseEntity.badRequest().body(alreadyActivatedAccountResponse);
        }
        if (request.getVerificationCode() != null) {
            if (Objects.equals(user.get().getActivateAccountToken(), request.getVerificationCode())) {
                user.get().setEnabled(true);
                userService.update(user.get());
                return ResponseEntity.ok(successFinishedActivateAccountResponse);
            } else {
                return ResponseEntity.badRequest().body(failedFinishedActivateAccountResponse);
            }
        } else {
            ResponseEntity<Map<String, String>> failedSendVerificationCodeResponse1 = checkSendVerificationCodePeriod(user.get());
            if (failedSendVerificationCodeResponse1 != null) return failedSendVerificationCodeResponse1;
            updateUserVerificationCodePeriod(user.get());
            mailService.sendMailActivateAccount(user.get());
            return ResponseEntity.ok(successSendActivateAccountResponse);
        }
    }

    @PostMapping("/change-email")
    ResponseEntity<Map<String, String>> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }

        if (userService.isEmailExist(request.getNewEmail()) && !Objects.equals(request.getNewEmail(), user.get().getEmail())) {
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }

        ResponseEntity<Map<String, String>> failedSendVerificationCodeResponse = checkSendVerificationCodePeriod(user.get());
        if (failedSendVerificationCodeResponse != null) return failedSendVerificationCodeResponse;
        updateUserVerificationCodePeriod(user.get());
        Optional<VerificationCode> verificationCode = userService.findVerificationCodeByUserAndVerificationType(user.get(), VerificationType.CHANGE_EMAIL);
        String token = UUID.randomUUID().toString().split("-")[0];
        userService.createOrUpdateVerificationCode(user.get(), token, verificationCode, VerificationType.CHANGE_EMAIL, request.getNewEmail());
        mailService.sendMailChangeEmail(user.get(), request.getNewEmail(), token);
        return ResponseEntity.ok(successSendChangeEmailResponse);
    }

    private void updateUserVerificationCodePeriod(User user) {
        user.setLastSendVerificationCodeDate(LocalDateTime.now());
        userService.update(user);
    }

    private static ResponseEntity<Map<String, String>> checkSendVerificationCodePeriod(User user) {
        if (user.getLastSendVerificationCodeDate() != null) {
            Duration duration = Duration.between(user.getLastSendVerificationCodeDate(), LocalDateTime.now());
            if (duration.toMinutes() < VerificationCode.SEND_VERIFICATION_CODE_PERIOD) {
                return ResponseEntity.badRequest().body(failedSendVerificationCodeResponse);
            }
        }
        return null;
    }

    private static ResponseEntity<Map<String, String>> checkMainPassword(String newPassword) {
        if (newPassword == null) {
            return ResponseEntity.badRequest().body(emptyNewPasswordResponse);
        }
        if (newPassword.length() < 8 || newPassword.length() > 128) {
            return ResponseEntity.badRequest().body(passwordLengthRangeResponse);
        }
        return null;
    }

}
