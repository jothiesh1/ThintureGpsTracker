package com.GpsTracker.Thinture.controller;


import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 30; // In minutes

    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, LocalDateTime> lastLoginTime = new HashMap<>();

    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        logger.info("Rendering login page.");
        return "login"; // Return login.html
    }

    // Handle login POST request
    @PostMapping("/login")
    public ModelAndView login(@RequestParam String usertype,
                              @RequestParam String username,
                              @RequestParam String password) {
        logger.info("Login attempt for username: {} with usertype: {}", username, usertype);

        if (isAccountLocked(username)) {
            return new ModelAndView("login", "error", "Account locked due to too many failed attempts. Try again later.");
        }

        Object user = findUserByType(usertype, username);
        if (user == null) {
            return incrementLoginAttempts(username);
        }

        if (!validatePassword(user, password)) {
            return incrementLoginAttempts(username);
        }

        // Reset login attempts on successful login
        loginAttempts.remove(username);
        lastLoginTime.put(username, LocalDateTime.now());

        // Authenticate user and set session
        authenticateUser(user, username, usertype);

        // Redirect user based on their role
        return redirectUserByRole(usertype);
    }

    // Determine if account is locked due to too many failed attempts
    private boolean isAccountLocked(String username) {
        if (loginAttempts.containsKey(username) && loginAttempts.get(username) >= MAX_ATTEMPTS) {
            LocalDateTime lockEndTime = lastLoginTime.get(username).plusMinutes(LOCK_TIME_DURATION);
            if (LocalDateTime.now().isBefore(lockEndTime)) {
                logger.warn("Account is locked for username: {}", username);
                return true;
            } else {
                // Unlock account after lock duration
                loginAttempts.put(username, 0);
            }
        }
        return false;
    }

    // Find the user by role type and username
    private Object findUserByType(String usertype, String username) {
        Object user = null;
        switch (usertype.toUpperCase()) {
            case "SUPERADMIN":
                user = superAdminRepository.findByEmail(username);
                break;
            case "ADMIN":
                user = adminRepository.findByEmailIgnoreCase(username);
                break;
            case "DEALER":
                user = dealerRepository.findByEmail(username);
                break;
            case "USER":
                user = userRepository.findByEmail(username);
                break;
            default:
                logger.error("Invalid user type: {}", usertype);
        }
        return user;
    }

    // Validate the provided password against the stored password
    private boolean validatePassword(Object user, String rawPassword) {
        String encodedPassword = extractPassword(user);
        boolean passwordMatches = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("Password matching result: {}", passwordMatches);
        return passwordMatches;
    }

    // Extract password from the user object
    private String extractPassword(Object user) {
        if (user instanceof SuperAdmin) {
            return ((SuperAdmin) user).getPassword();
        } else if (user instanceof Admin) {
            return ((Admin) user).getPassword();
        } else if (user instanceof Dealer) {
            return ((Dealer) user).getPassword();
        } else if (user instanceof com.GpsTracker.Thinture.model.User) {
            return ((com.GpsTracker.Thinture.model.User) user).getPassword();
        }
        return null;
    }

    // Increment login attempts and return error message
    private ModelAndView incrementLoginAttempts(String username) {
        loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
        logger.warn("Invalid login attempt for username: {}", username);
        return new ModelAndView("login", "error", "Invalid username or password.");
    }

    // Authenticate user and set the security context
    private void authenticateUser(Object user, String username, String usertype) {
        String role = "ROLE_" + usertype.toUpperCase();
        User authenticatedUser = new User(username, extractPassword(user), Collections.singletonList(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities()));
        logger.info("Login successful for username: {}", username);
    }

    // Redirect user based on their role
    private ModelAndView redirectUserByRole(String usertype) {
        switch (usertype.toUpperCase()) {
            case "SUPERADMIN":
                return new ModelAndView("redirect:/dashboard");
            case "ADMIN":
                return new ModelAndView("redirect:/dashboard");
            case "DEALER":
                return new ModelAndView("redirect:/dashboard_dealer");
            case "USER":
                return new ModelAndView("redirect:/map");
            default:
                return new ModelAndView("redirect:/login");
        }
    }

    // Redirect after login based on the role
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_SUPERADMIN")) {
            return "redirect:/dashboard";
        } else if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (request.isUserInRole("ROLE_DEALER")) {
            return "redirect:/dashboard_dealer";
        } else if (request.isUserInRole("ROLE_USER")) {
            return "redirect:/map";
        } else {
            return "redirect:/login";
        }
    }
}
