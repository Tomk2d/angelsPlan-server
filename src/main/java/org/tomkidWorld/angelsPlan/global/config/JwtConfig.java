package org.tomkidWorld.angelsPlan.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.Key;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class JwtConfig {
    @Bean
    public Key jwtSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
} 