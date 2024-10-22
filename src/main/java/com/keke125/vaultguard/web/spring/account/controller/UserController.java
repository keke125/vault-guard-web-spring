package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.SignupRequest;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.*;

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
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignupRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (!Objects.equals(request.getUsername(), user.get().getUsername())) {
            return ResponseEntity.badRequest().body(usernameNotAllowedResponse);
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            return ResponseEntity.badRequest().body(passwordLengthRangeResponse);
        }
        if (!userService.isEmailNonExist(request.getEmail()) && !Objects.equals(request.getEmail(), user.get().getEmail())) {
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        user.get().setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        user.get().setEmail(request.getEmail());
        userService.update(user.get());
        return ResponseEntity.ok(successUpdateResponse);
    }

}
