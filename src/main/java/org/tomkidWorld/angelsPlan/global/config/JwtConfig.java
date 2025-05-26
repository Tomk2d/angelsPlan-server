package org.tomkidWorld.angelsPlan.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String secret;
    
    @Bean
    public Key jwtSecretKey() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        return new SecretKeySpec(keyBytes, "HmacSHA512");
    }
} 