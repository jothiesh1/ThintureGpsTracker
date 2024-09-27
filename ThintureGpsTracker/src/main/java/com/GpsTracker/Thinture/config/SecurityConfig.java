package com.GpsTracker.Thinture.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.GpsTracker.Thinture.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain...");
        
        http
            .csrf().disable()
            .authorizeRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers("/login", "/resources/**").permitAll();
                logger.info("Permit all requests to /login and static resources.");
                
                authorizeRequests
                    .anyRequest().authenticated();
                logger.info("All other requests require authentication.");
            })
            .formLogin(formLogin -> {
                formLogin
                    .loginPage("/login")
                    .failureUrl("/login?error=true")
                    .defaultSuccessUrl("/dashboard", true)
                    .permitAll();
                
                logger.info("Login configuration set with custom login page, failure URL, and success URL.");
            })
            .logout(logout -> {
                logout
                    .permitAll();
                logger.info("Logout is permitted for all users.");
            });
        
        logger.info("Security filter chain configured.");
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configuring NoOpPasswordEncoder for plain text passwords.");
        return NoOpPasswordEncoder.getInstance();  // Using NoOp for plain text passwords
    }
}




