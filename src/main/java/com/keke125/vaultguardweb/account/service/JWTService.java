package com.keke125.vaultguardweb.account.service;


import com.keke125.vaultguardweb.account.request.AuthRequest;
import com.keke125.vaultguardweb.util.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    private static AuthenticationManager authenticationManager;

    public JWTService(AuthenticationManager authenticationManager, AppConfig appConfig) {
        JWTService.authenticationManager = authenticationManager;
        if (appConfig.getJWTKey() != null) {
            TOKEN_SECRET = appConfig.getJWTKey();
        } else {
            TOKEN_SECRET = "cuAihCz53DZRjZwbsGcZJ2Ai6At+T142uphtJMsk7iQ=";
        }
    }

    // base64 encoded string
    // privateKey
    private static String TOKEN_SECRET;

    public static SecretKey getSigningKey() {
        byte[] encodeKey = Decoders.BASE64.decode(JWTService.TOKEN_SECRET);
        return Keys.hmacShaKeyFor(encodeKey);
    }

    public static String generateJWT(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authentication = authenticationManager.authenticate(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // millisecond
        // one day
        long expireTime = 1440 * 60 * 1000;
        Date current = new Date();
        Date expiration = new Date(current.getTime() + expireTime);

        SecretKey secretKey = getSigningKey();
        return Jwts.builder().issuer("com.keke125.vaultguardweb").subject(userDetails.getUsername()).expiration(expiration).notBefore(current).issuedAt(current).id(UUID.randomUUID().toString()).signWith(secretKey).compact();
    }

    public static Jws<Claims> parseJWT(String jwt) {
        SecretKey secretKey = getSigningKey();
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt);
    }

    public static String validateTokenAndGetUsername(final String token) {
        try {
            Jws<Claims> claims = JWTService.parseJWT(token);
            return claims.getPayload().getSubject();
        } catch (JwtException ex) {
            return null;
        }
    }

}
