package com.keke125.vaultguard.web.spring.password.controller;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.password.request.SavePasswordRequest;
import com.keke125.vaultguard.web.spring.password.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.userNotFoundResponse;
import static com.keke125.vaultguard.web.spring.password.ResponseMessage.passwordDuplicatedResponse;
import static com.keke125.vaultguard.web.spring.password.ResponseMessage.successSavePasswordResponse;

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
        User user = userIdentity.getCurrentUser();
        if (user == UserIdentity.EMPTY_USER) {
            return ResponseEntity.badRequest().body(userNotFoundResponse);
        }
        if (passwordService.isPasswordExists(request.getName(), request.getUsername())) {
            return ResponseEntity.badRequest().body(passwordDuplicatedResponse);
        }
        passwordService.savePassword(request, user);
        return ResponseEntity.ok(successSavePasswordResponse);
    }
}
