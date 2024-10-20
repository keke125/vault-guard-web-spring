package com.keke125.vaultguard.web.spring.account.response;

import com.keke125.vaultguard.web.spring.account.entity.Role;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserIdentity {
    public static final User EMPTY_USER = new User();
    private final UserService userService;

    public UserIdentity(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(principal.getUsername()).getRoles().contains(Role.USER) ? userService.findByUsername(principal.getUsername()) : EMPTY_USER;
    }

    public String getUsername() {
        return getCurrentUser().getUsername();
    }

}