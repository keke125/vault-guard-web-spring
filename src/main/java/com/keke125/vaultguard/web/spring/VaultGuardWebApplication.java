package com.keke125.vaultguard.web.spring;

import com.keke125.vaultguard.web.spring.util.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableAsync
public class VaultGuardWebApplication {

    private final AppConfig appConfig;

    public VaultGuardWebApplication(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(VaultGuardWebApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins(appConfig.getWebUrl()).allowedMethods("GET", "POST", "PATCH", "DELETE");
            }
        };
    }
}
