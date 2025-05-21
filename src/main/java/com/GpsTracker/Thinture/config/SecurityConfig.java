package com.GpsTracker.Thinture.config;

import com.GpsTracker.Thinture.security.CookieValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;



@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CookieValidationFilter cookieValidationFilter;

    public SecurityConfig(CookieValidationFilter cookieValidationFilter) {
        this.cookieValidationFilter = cookieValidationFilter;
    }

    // ✅ 1. Allow public access to /mapHere via separate filter chain
    @Bean
    @Order(0)
    public SecurityFilterChain mapHereFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(new AntPathRequestMatcher("/mapHere/**")) // ✅ use Ant-style pattern matcher
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }


    // ✅ 2. API security chain
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/ota/firmware/update_check").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(cookieValidationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("✅ API filter chain applied.");
        return http.build();
    }

    // ✅ 3. Default web UI filter chain
    @Bean
    @Order(2)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/forgot_password",
                    "/reset_password",
                    "/resources/**",
                    "/map",                   // ✅ other public pages if needed
                    "/THINTURE_IMAGE/**",
                    "/css/**",
                    "/js/**",
                    "/static/**",
                    "/firmware/**",
                    "/firmware/main.hex",
                    "/map",            // ✅ Thymeleaf route, public access
                    "/map.html", 
                    "/access-denied"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()

                // ✅ Role-based access
                .requestMatchers("/superadmin/page/**").hasRole("SUPERADMIN")
                .requestMatchers("/admin/page/**").hasRole("ADMIN")
                .requestMatchers("/dealer/page/**").hasRole("DEALER")
                .requestMatchers("/client/page/**").hasRole("CLIENT")
                .requestMatchers("/user/page/**").hasRole("USER")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/noop")
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .addFilterBefore(cookieValidationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("✅ Default web filter chain applied.");
        return http.build();
    }

    // ✅ Authentication manager setup
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        return new ProviderManager(List.of(authenticationProvider(userDetailsService)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // ⚠️ use BCrypt in production
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

