package com.keke125.vaultguard.web.spring.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
@Data
public class AppConfig {
    // View the src/main/resources/application.properties file for more details
    private String idForEncode;
    private String JWTKey;
    private String JWTIssuer;
    private String sendFromName;
    private String sendFromAddress;
    private String webUrl;
}