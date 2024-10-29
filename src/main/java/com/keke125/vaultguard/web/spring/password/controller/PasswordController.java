package com.keke125.vaultguard.web.spring.password.controller;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.password.entity.Password;
import com.keke125.vaultguard.web.spring.password.request.SavePasswordRequest;
import com.keke125.vaultguard.web.spring.password.request.UpdatePasswordRequest;
import com.keke125.vaultguard.web.spring.password.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.userNotFoundMessage;
import static com.keke125.vaultguard.web.spring.password.ResponseMessage.*;

@RestController
@RequestMapping(value = "/api/v1/password", produces = MediaType.APPLICATION_JSON_VALUE)
public class PasswordController {
    private final PasswordService passwordService;
    private final UserIdentity userIdentity;

    public PasswordController(PasswordService passwordService, UserIdentity userIdentity) {
        this.passwordService = passwordService;
        this.userIdentity = userIdentity;
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> savePassword(@Valid @RequestBody SavePasswordRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (passwordService.isPasswordExists(request.getName(), request.getUsername(), user.get().getUid())) {
            return ResponseEntity.badRequest().body(passwordDuplicatedResponse);
        }
        passwordService.savePassword(request, user.get());
        return ResponseEntity.ok(successSavePasswordResponse);
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        Optional<Password> oldPassword = passwordService.findByUidAndUserUid(request.getUid(), user.get().getUid());
        if (oldPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(passwordNotFoundResponse);
        }
        if ((!Objects.equals(request.getName(), oldPassword.get().getName()) && !Objects.equals(request.getUsername(), oldPassword.get().getUsername())) && passwordService.isPasswordExists(request.getName(), request.getUsername(), user.get().getUid())) {
            return ResponseEntity.badRequest().body(passwordDuplicatedResponse);
        }
        passwordService.updatePassword(request, user.get(), oldPassword.get());
        return ResponseEntity.ok(successUpdatePasswordResponse);
    }

    @GetMapping("/passwords")
    public ResponseEntity<List<Password>> getAllPasswords() {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        return ResponseEntity.ok(passwordService.findAllByUserUid(user.get().getUid()));
    }

    @GetMapping("/password/{uid}")
    public ResponseEntity<Password> getPasswordByUid(@PathVariable String uid) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (passwordService.findByUidAndUserUid(uid, user.get().getUid()).isPresent()) {
            return ResponseEntity.ok(passwordService.findByUidAndUserUid(uid, user.get().getUid()).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
