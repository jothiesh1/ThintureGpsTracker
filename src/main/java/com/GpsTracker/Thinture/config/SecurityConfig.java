package com.GpsTracker.Thinture.config;
import java.util.List;
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

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔐 Configuring security filter chain...");

        http
            .csrf().disable()
            .authorizeRequests(auth -> {
                auth
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
                        "/firmware/main.hex"   // ✅ Public access to OTA hex file
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()

                    // Role-based dashboards
                    .requestMatchers("/dashboard").hasAnyRole("SUPERADMIN")
                    .requestMatchers("/dashboard_admin").hasRole("ADMIN")
                    .requestMatchers("/dashboard_dealer").hasRole("DEALER")
                    .requestMatchers("/dashboard_client").hasRole("CLIENT")
                    .requestMatchers("/dashboard_user").hasRole("USER")
                    // ✅ Logs API + Logs Viewer page access for SUPERADMIN and ADMIN only
                    .requestMatchers("/api/logs/**").hasAnyRole("SUPERADMIN", "ADMIN")
                    .requestMatchers("/serverLogs.html").hasAnyRole("SUPERADMIN", "ADMIN")
                    // Everything else needs authentication
                    .anyRequest().authenticated();

                logger.info("🔐 Role-based access configured.");
            })
            .formLogin(form -> {
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/noop") // disables default Spring login
                    .failureUrl("/login?error=true")
                    .permitAll();
                logger.info("🔐 Login form configuration set.");
            })
            .logout(logout -> {
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll();
                logger.info("🔐 Logout configuration set.");
            });

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
        logger.warn("⚠ Using NoOpPasswordEncoder: Plain text passwords enabled.");
        return NoOpPasswordEncoder.getInstance(); // only for testing
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

