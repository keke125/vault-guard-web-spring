package com.keke125.vaultguard.web.spring.account.service;


import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.request.AuthRequest;
import com.keke125.vaultguard.web.spring.util.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class JWTService {

    private static AuthenticationManager authenticationManager;
    private static AppConfig appConfig;

    public JWTService(AuthenticationManager authenticationManager, AppConfig appConfig) {
        JWTService.authenticationManager = authenticationManager;
        JWTService.appConfig = appConfig;
        if (!Objects.equals(appConfig.getJWTKey(), "${VGW_JWT_KEY}")) {
            TOKEN_SECRET = appConfig.getJWTKey();
        } else {
            TOKEN_SECRET = "MzkyMGI4YzlmZDk4MTllNWZjOWUwOWYyZDhjMmMyNzUxMTQ1YWMxODgyZTZmYTNhODdiYWU3Yzk3OWZiNjNlOWM0MjdhMjg5NmQwNzdiYTY2ZWM1MGVkZDIzYzIwZWEzMjEyMWJiZThkYmM2M2Y1YzgwZTMzZDQ0MDc3MGFkYzFhZDExOWYyMDI1YWM2MWIwNTY2OTBhOWQwMTMzOGM5OWQ0ZGFmYjQ4MTdlNDc3OGMzZGQ1ZWU0MTRjZmMxNTExMDdmMTU5OGM5MWUzN2FiNGU0MzRlNWNiMjZlNGE4YmUwMTE0MmUzZjNiZTE0ZjExNWY5YTdmM2E4MjI2Nzg5MTQ0N2QyMmY3NGRkNmY4ZGM2ZDUxYjI3M2ViMjk4NjA0OTY3ZTViYzIyMTYwMzhlM2VlOTU5ZTcwMzk1Yzk1YTg5MjE1NDExMmRhNGRjN2Y2Nzk1ODNjZDYxNTVlMzhiYzVjZTg4ZjQ5ZDViNGZjYTE5MjM0ZTliMzI3ZDdkYzQ1YWQ5ZjlmYWUyMzRlYzRjN2RiM2ViNzU3OTE0YzRjMDU4MGRhMWZkMWY5MGVhZTQ5Yjg5NGIwZjExYzIzODk5ZmM2NjRlZjViMTJjOTA1Njc5NzJhM2U0NDJjOWZkMzg1YzhkYTJkZDViMmY4MDQ1ZDA1MzU1Y2NhMzVjYTEzOTY=";
            System.err.println("Ensure that the environment variable VGW_JWT_KEY is correctly configured!");
        }
    }

    // JWT KEY (base64 string)
    private static String TOKEN_SECRET;

    public static SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(JWTService.TOKEN_SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public static String generateJWT(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authentication = authenticationManager.authenticate(authentication);
        User userDetails = (User) authentication.getPrincipal();
        // millisecond
        // one day
        long expireTime = 1440 * 60 * 1000;
        Date current = new Date();
        Date expiration = new Date(current.getTime() + expireTime);

        SecretKey secretKey = getSigningKey();
        return Jwts.builder().issuer(appConfig.getJWTIssuer()).subject(userDetails.getUid()).expiration(expiration).notBefore(current).issuedAt(current).id(UUID.randomUUID().toString()).signWith(secretKey).compact();
    }

    public static Jws<Claims> parseJWT(String jwt) {
        SecretKey secretKey = getSigningKey();
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt);
    }

    public static String validateTokenAndGetUserUid(final String token) {
        try {
            Jws<Claims> claims = JWTService.parseJWT(token);
            return claims.getPayload().getSubject();
        } catch (JwtException ex) {
            return null;
        }
    }

}
