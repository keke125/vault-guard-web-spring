package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.PasswordResetToken;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.ResetPasswordRequest;
import com.keke125.vaultguard.web.spring.account.request.UpdateUserRequest;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import com.keke125.vaultguard.web.spring.mail.service.MailService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/reset")
    ResponseEntity<Map<String, String>> resetPasswordByEmail(@Valid @RequestBody ResetPasswordRequest request) {
        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (request.getToken() != null) {
            if (userService.validatePasswordResetToken(user.get(), request.getToken())) {
                ResponseEntity<Map<String, String>> checkMainPasswordResponse = checkMainPassword(request.getNewPassword());
                if (checkMainPasswordResponse != null) return checkMainPasswordResponse;
                user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getNewPassword()));
                userService.update(user.get());
                return ResponseEntity.ok(successFinishedResetResponse);
            } else {
                return ResponseEntity.badRequest().body(failedFinishedResetResponse);
            }
        } else {
            Optional<PasswordResetToken> passwordResetToken = userService.findPasswordResetTokenByUser(user.get());
            String token = UUID.randomUUID().toString();
            if (passwordResetToken.isPresent()) {
                userService.updatePasswordResetTokenForUser(user.get(), token, passwordResetToken.get());
            } else {
                userService.createPasswordResetTokenForUser(user.get(), token);
            }
            mailService.sendMailResetPassword(user.get(), token);
            return ResponseEntity.ok(successSendResetPasswordResponse);
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
