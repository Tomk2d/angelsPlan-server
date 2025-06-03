package org.tomkidWorld.angelsPlan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 모든 /api/** 경로에 대해
                .allowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:5173", 
                    "http://localhost:8080",
                    "http://localhost:8081",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:8080",
                    "http://127.0.0.1:8081"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // 1시간 동안 preflight 결과를 캐시
    }
} 