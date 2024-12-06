package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.UpdateUserRequest;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
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

    private final UserIdentity userIdentity;

    public UserController(UserService userService, UserIdentity userIdentity) {
        this.userService = userService;
        this.userIdentity = userIdentity;
    }

    @GetMapping("/user")
    ResponseEntity<User> getUserProfileByJWT() {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        return ResponseEntity.ok(user.get());
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
            if (request.getNewPassword() == null) {
                return ResponseEntity.badRequest().body(emptyNewPasswordResponse);
            }
            if (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 128) {
                return ResponseEntity.badRequest().body(passwordLengthRangeResponse);
            }
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

}
