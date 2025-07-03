package com.GpsTracker.Thinture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)  // 🚨 IMPORTANT: This makes this config have higher priority
public class ApiSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityConfig.class);

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔒 Configuring API Security Filter Chain...");

        http
            .securityMatcher("/api/**")  // ✅ Only applies to /api/ URLs
            .csrf().disable()
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                    .requestMatchers("/api/superadmin/**").hasRole("SUPERADMIN")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/dealer/**").hasRole("DEALER")
                    .requestMatchers("/api/client/**").hasRole("CLIENT")
                    .requestMatchers("/api/user/**").hasRole("USER")
                    .anyRequest().authenticated();
            });

        logger.info("✅ API Security Filter Chain configured.");
        return http.build();
    }
}
