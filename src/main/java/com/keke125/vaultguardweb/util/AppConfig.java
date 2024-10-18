package com.keke125.vaultguardweb.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
@Data
public class AppConfig {

    // default password encoder can be set by idForEncode
    // BCrypt pbkdf2 argon2
    // @Value("{security.password.encoder:argon2}")
    private String idForEncode;
    private String JWTKey;

}