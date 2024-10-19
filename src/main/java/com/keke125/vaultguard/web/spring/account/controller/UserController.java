package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.SignupRequest;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @ResponseBody
    User getUserProfileByJWT() {
        return userService.findByUsername(userIdentity.getUsername());
    }

    @PatchMapping("/user")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignupRequest request) {
        User user = userService.findByUsername(userIdentity.getUsername());
        if (!Objects.equals(request.getUsername(), user.getUsername())) {
            return ResponseEntity.badRequest().body(usernameNotAllowedResponse);
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            return ResponseEntity.badRequest().body(passwordLengthRangeResponse);
        }
        if (!userService.isEmailNonExist(request.getEmail()) && !Objects.equals(request.getEmail(), user.getEmail())) {
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        user.setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        user.setEmail(request.getEmail());
        userService.update(user);
        return ResponseEntity.ok(successUpdateResponse);
    }

}
