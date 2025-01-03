package com.keke125.vaultguard.web.spring.account.config;

import com.keke125.vaultguard.web.spring.account.service.JWTService;
import com.keke125.vaultguard.web.spring.account.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public JWTRequestFilter(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Look for AUTHORIZATION
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);
        final String userUid = JWTService.validateTokenAndGetUserUid(token);
        if (userUid == null) {
            // Validation failed or token expired
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // check if user exists
            final UserDetails userDetails = userDetailsServiceImpl.loadUserByUserUid(userUid);
            // Set user details on spring security context
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception) {
            filterChain.doFilter(request, response);
            return;
        }

        // continue with authenticated user
        filterChain.doFilter(request, response);
    }
}
