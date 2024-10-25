package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Map to store login attempts
    private Map<String, Integer> loginAttempts = new HashMap<>();

    // Map to store last login time
    private Map<String, LocalDateTime> lastLoginTime = new HashMap<>();

    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        logger.info("Rendering login page.");
        return "login";  // Return login.html
    }

    // Handle login
    @PostMapping("/login")
    public ModelAndView login(@RequestParam String usertype,
                              @RequestParam String username,
                              @RequestParam String password) {
        logger.info("Login attempt for username: {} with usertype: {}", username, usertype);

        Object user = null;
        Long userId = null;

        // Check if the account is locked due to too many failed login attempts
        if (loginAttempts.containsKey(username) && loginAttempts.get(username) >= 5) {
            logger.warn("Account locked for username: {} due to too many failed attempts.", username);
            return new ModelAndView("login", "error", "Account locked due to too many failed login attempts. Try again later.");
        }

        // Fetch user by role type and get the ID
        switch (usertype.toUpperCase()) {
            case "SUPERADMIN":
                user = superAdminRepository.findByEmail(username);
                if (user != null && user instanceof SuperAdmin) {
                    userId = ((SuperAdmin) user).getId();
                    logger.info("SuperAdmin login successful. ID: {}", userId);
                } else {
                    logger.error("SuperAdmin login failed for username: {}", username);
                    loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);  // Increment login attempts
                    return new ModelAndView("login", "error", "Invalid username or password.");
                }
                break;

            case "ADMIN":
                user = adminRepository.findByEmailIgnoreCase(username);
                if (user != null && user instanceof Admin) {
                    userId = ((Admin) user).getId();
                    logger.info("Admin login successful. ID: {}", userId);
                } else {
                    logger.error("Admin login failed for username: {}", username);
                    loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);  // Increment login attempts
                    return new ModelAndView("login", "error", "Invalid username or password.");
                }
                break;

            case "DEALER":
                user = dealerRepository.findByEmail(username);
                if (user == null) {
                    logger.error("Dealer login failed for username: {}", username);
                    loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);  // Increment login attempts
                    return new ModelAndView("login", "error", "Invalid username or password.");
                }
                break;

            case "USER":
                user = userRepository.findByEmail(username);
                if (user == null) {
                    logger.error("User login failed for username: {}", username);
                    loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);  // Increment login attempts
                    return new ModelAndView("login", "error", "Invalid username or password.");
                }
                break;

            default:
                logger.error("Invalid user type: {}", usertype);
                return new ModelAndView("login", "error", "Invalid user type.");
        }

        // Validate password
        if (user != null) {
            String encodedPassword;
            if (user instanceof SuperAdmin) {
                encodedPassword = ((SuperAdmin) user).getPassword();
            } else if (user instanceof Admin) {
                encodedPassword = ((Admin) user).getPassword();
            } else if (user instanceof Dealer) {
                encodedPassword = ((Dealer) user).getPassword();
            } else {
                encodedPassword = ((com.GpsTracker.Thinture.model.User) user).getPassword();
            }

            boolean passwordMatches = passwordEncoder.matches(password, encodedPassword);
            logger.info("Password matching result for username {}: {}", username, passwordMatches);

            if (passwordMatches) {
                logger.info("Login successful for username: {} with usertype: {}", username, usertype);

                String role = "ROLE_" + usertype.toUpperCase();

                User authenticatedUser = new User(username, encodedPassword,
                        Collections.singletonList(new SimpleGrantedAuthority(role)));

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities())
                );

                loginAttempts.remove(username);  // Reset login attempts on successful login
                lastLoginTime.put(username, LocalDateTime.now()); // Record last login time

             // Redirect based on role
                switch (usertype.toUpperCase()) {
                    case "SUPERADMIN":
                    case "ADMIN":
                        logger.info("Redirecting to /dashboard");
                        return new ModelAndView("redirect:/dashboard");
                    case "DEALER":
                        logger.info("Redirecting to /dashboard_dealer");
                        return new ModelAndView("redirect:/dashboard_dealer");
                    case "USER":
                        logger.info("Redirecting to /map");
                        return new ModelAndView("redirect:/map");
                    default:
                        logger.warn("Usertype not found, redirecting to /login");
                        return new ModelAndView("redirect:/login");
                
           
                }
            } else {
                logger.error("Invalid password for username: {}", username);
                loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);  // Increment login attempts
                return new ModelAndView("login", "error", "Invalid password.");
            }
        } else {
            logger.error("Login failed for username: {}", username);
            return new ModelAndView("login", "error", "Invalid username or password.");
        }
    }
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_SUPERADMIN")) {
            return "redirect:/dashboard";  // Correct path for the dashboard
        } else if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/dashboard";  // Corrected the path by adding a forward slash
        } else if (request.isUserInRole("ROLE_DEALER")) {
            return "redirect:/dashboard_dealer";
        } else if (request.isUserInRole("ROLE_USER")) {
            return "redirect:/map";
        } else {
            return "redirect:/login";  // If no role matches
        }
    }
    
}
