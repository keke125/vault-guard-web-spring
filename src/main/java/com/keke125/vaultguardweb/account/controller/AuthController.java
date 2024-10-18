package com.keke125.vaultguardweb.account.controller;

import com.keke125.vaultguardweb.account.entity.Role;
import com.keke125.vaultguardweb.account.entity.User;
import com.keke125.vaultguardweb.account.request.AuthRequest;
import com.keke125.vaultguardweb.account.request.SignupRequest;
import com.keke125.vaultguardweb.account.service.JWTService;
import com.keke125.vaultguardweb.account.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.keke125.vaultguardweb.account.ResponseMessage.*;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        if (!userService.isUsernameNonExist(request.getUsername())) {
            if (!userService.isEmailNonExist(request.getEmail())) {
                return ResponseEntity.badRequest().body(emailAndUsernameDuplicatedResponse);
            }
            return ResponseEntity.badRequest().body(usernameDuplicatedResponse);
        }
        if (!userService.isEmailNonExist(request.getEmail())) {
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        userService.store(newUser);
        return ResponseEntity.ok(successSignupResponse);
    }

}
