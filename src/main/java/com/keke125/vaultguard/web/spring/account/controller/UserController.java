package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.VerificationCode;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.entity.VerificationType;
import com.keke125.vaultguard.web.spring.account.request.ActivateAccountRequest;
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

    @PatchMapping("/user")
    public ResponseEntity<Map<String, String>> updateUser(@RequestParam String type, @Valid @RequestBody UpdateUserRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }

        if (request.getOldPassword() == null) {
            return ResponseEntity.badRequest().body(emptyPasswordResponse);
        }

        if (!userService.checkMainPassword(user.get().getUsername(), request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMainPasswordResponse);
        }

        if (Objects.equals(type, "password")) {
            ResponseEntity<Map<String, String>> checkMainPasswordResponse = checkMainPassword(request.getNewPassword());
            if (checkMainPasswordResponse != null) return checkMainPasswordResponse;
            user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getNewPassword()));
        } else if (Objects.equals(type, "email")) {
            if (request.getEmail() == null) {
                return ResponseEntity.badRequest().body(emptyEmailResponse);
            }
            if (!userService.isEmailNonExist(request.getEmail()) && !Objects.equals(request.getEmail(), user.get().getEmail())) {
                return ResponseEntity.badRequest().body(emailDuplicatedResponse);
            }
            user.get().setEmail(request.getEmail());
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
        if (request.getToken() != null) {
            if (userService.validateVerificationCode(user.get(), request.getToken(), VerificationType.RESET_PASSWORD)) {
                ResponseEntity<Map<String, String>> checkMainPasswordResponse = checkMainPassword(request.getNewPassword());
                if (checkMainPasswordResponse != null) return checkMainPasswordResponse;
                user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getNewPassword()));
                userService.update(user.get());
                return ResponseEntity.ok(successFinishedResetResponse);
            } else {
                return ResponseEntity.badRequest().body(failedFinishedResetResponse);
            }
        } else {
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
        if (request.getToken() != null) {
            if (Objects.equals(user.get().getActivateAccountToken(), request.getToken())) {
                user.get().setEnabled(true);
                userService.update(user.get());
                return ResponseEntity.ok(successFinishedActivateAccountResponse);
            } else {
                return ResponseEntity.badRequest().body(failedFinishedActivateAccountResponse);
            }
        } else {
            mailService.sendMailActivateAccount(user.get());
            return ResponseEntity.ok(successSendActivateAccountResponse);
        }
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
