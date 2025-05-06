package com.GpsTracker.Thinture.config;

import com.GpsTracker.Thinture.security.CookieValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CookieValidationFilter cookieValidationFilter;

    public SecurityConfig(CookieValidationFilter cookieValidationFilter) {
        this.cookieValidationFilter = cookieValidationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔐 Configuring security filter chain...");

        http
            .csrf().disable()
            .authorizeRequests(auth -> {
                auth
                    // ✅ Public URLs (no login required)
                    .requestMatchers(
                        "/login",
                        "/forgot_password",
                        "/reset_password",
                        "/resources/**",
                        "/map",
                        "/THINTURE_IMAGE/**",
                        "/css/**",
                        "/js/**",
                        "/static/**",
                        "/firmware/**",
                        "/firmware/main.hex",
                        "/access-denied"
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()

                    // 🚨 ✅ ✅ 🚨 BLOCK *ONLY* PAGE URLs (NOT APIs)
                    // Make sure your pages are grouped like this:
                    .requestMatchers("/superadmin/page/**").hasRole("SUPERADMIN")
                    .requestMatchers("/admin/page/**").hasRole("ADMIN")
                    .requestMatchers("/dealer/page/**").hasRole("DEALER")
                    .requestMatchers("/client/page/**").hasRole("CLIENT")
                    .requestMatchers("/user/page/**").hasRole("USER")

                    // ✅ Everything else (like APIs) stays allowed if authenticated
                    .anyRequest().authenticated();
            })
            .formLogin(form -> {
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/noop")
                    .failureUrl("/login?error=true")
                    .permitAll();
            })
            .logout(logout -> {
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll();
            })
            .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))
            .addFilterBefore(cookieValidationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("✅ Security filter chain initialized successfully.");
        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        logger.info("🔧 Setting up Authentication Provider...");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        logger.info("✅ Authentication Provider ready.");
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        logger.info("🔧 Initializing Authentication Manager...");
        return new ProviderManager(List.of(authenticationProvider(userDetailsService)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.warn("⚠ Using NoOpPasswordEncoder: Plain text passwords enabled. ⚠");
        return NoOpPasswordEncoder.getInstance(); // Change to BCrypt in production
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

