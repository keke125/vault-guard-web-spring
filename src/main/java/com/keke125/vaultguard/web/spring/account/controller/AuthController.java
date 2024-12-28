package com.keke125.vaultguard.web.spring.account.controller;

import com.keke125.vaultguard.web.spring.account.entity.Role;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.AuthRequest;
import com.keke125.vaultguard.web.spring.account.request.SignupRequest;
import com.keke125.vaultguard.web.spring.account.service.JWTService;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import com.keke125.vaultguard.web.spring.mail.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.*;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserService userService;
    private final MailService mailService;

    public AuthController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }


    @PostMapping("/log-in")
    public ResponseEntity<Map<String, String>> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = JWTService.generateJWT(request);
        Map<String, String> response = Collections.singletonMap("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignupRequest request) {
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            return ResponseEntity.badRequest().body(passwordLengthRangeResponse);
        }
        newUser.setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        newUser.setRoles(roles);
        newUser.setEnabled(false);
        newUser.setActivateAccountToken(UUID.randomUUID().toString());
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setLastSendVerificationCodeDate(LocalDateTime.now());
        if (!userService.isUsernameNonExist(request.getUsername())) {
            if (!userService.isEmailNonExist(request.getEmail())) {
                return ResponseEntity.badRequest().body(emailAndUsernameDuplicatedResponse);
            }
            return ResponseEntity.badRequest().body(usernameDuplicatedResponse);
        }
        if (!userService.isEmailNonExist(request.getEmail())) {
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        mailService.sendMailActivateAccount(newUser);
        userService.store(newUser);
        return ResponseEntity.ok(successSignupResponse);
    }

}
