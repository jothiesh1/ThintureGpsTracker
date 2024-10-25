package com.GpsTracker.Thinture.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;	
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain...");

        http
            .csrf().disable()  // Disable CSRF for development purposes
            .authorizeRequests(authorizeRequests -> {
                // Permit login, static resources, and map access for all users
                authorizeRequests
                    .requestMatchers("/login", "/resources/**", "/map").permitAll()
                    // Allow both SUPERADMIN and ADMIN to access /dashboard
                    .requestMatchers("/dashboard").hasAnyRole("SUPERADMIN", "ADMIN")
                    // Restrict /dashboard_dealer access to DEALER role
                    .requestMatchers("/dashboard_dealer").hasRole("DEALER")
                    // All other requests require authentication
                    .anyRequest().authenticated();
                logger.info("Role-based access configured.");
            })
            .formLogin(formLogin -> {
                formLogin
                    .loginPage("/login")
                    .failureUrl("/login?error=true")  // Redirect on login failure
                    .defaultSuccessUrl("/default", true)  // Redirect based on roles
                    .permitAll();  // Allow access to the login page for all users
                logger.info("Login form configuration set.");
            })
            .logout(logout -> {
                logout
                    .permitAll();  // Allow logout for all users
                logger.info("Logout configuration set.");
            });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Using NoOpPasswordEncoder for testing purposes (insecure).");
        return NoOpPasswordEncoder.getInstance();  // Insecure, use only for testing
    }
}



/*
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

*/


