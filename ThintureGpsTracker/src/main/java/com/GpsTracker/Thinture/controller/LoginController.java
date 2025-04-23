package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.service.UserTypeFilterService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired private UserTypeFilterService userTypeFilterService;
    @Autowired private PasswordEncoder passwordEncoder;

    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Map<String, LocalDateTime> lastLoginTime = new HashMap<>();

    @GetMapping("/login")
    public String showLoginPage() {
        logger.info("🌐 Rendering login page.");
        return "login";
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String usertype,
                              HttpServletRequest request) {
        logger.info("🔐 Login POST request received → Username: [{}], Selected Type: [{}]", username, usertype);

        if (loginAttempts.getOrDefault(username, 0) >= 5) {
            logger.warn("⛔ Account LOCKED: [{}] exceeded maximum login attempts", username);
            return new ModelAndView("login", "error", "Account locked. Too many failed attempts.");
        }

        var result = userTypeFilterService.findUserAndTypeByEmail(username);
        if (result == null) {
            logger.error("❌ Login failed → No user found for: [{}]", username);
            return new ModelAndView("login", "error", "Invalid username or password.");
        }

        Object userObj = result.userObject();
        String actualUserType = result.actualUserType();
        Long userId = result.userId();

        logger.info("✅ User found → ID: [{}], Type: [{}], Email: [{}]", userId, actualUserType, username);

        // Enforce selected user type match
        if (!usertype.equalsIgnoreCase(actualUserType)) {
            logger.warn("🚫 MISMATCH: Selected Type: [{}], Actual Type: [{}]", usertype, actualUserType);
            return new ModelAndView("login", "error", "Selected user type doesn't match account.");
        }

        logger.debug("🔍 Verifying password for user: [{}]", username);

        String encodedPassword = switch (actualUserType) {
            case "SUPERADMIN" -> ((SuperAdmin) userObj).getPassword();
            case "ADMIN"      -> ((Admin) userObj).getPassword();
            case "DEALER"     -> ((Dealer) userObj).getPassword();
            case "CLIENT"     -> ((Client) userObj).getPassword();
            case "USER"       -> ((com.GpsTracker.Thinture.model.User) userObj).getPassword();
            default           -> {
                logger.error("❓ Unknown user type: [{}] - Login rejected.", actualUserType);
                yield "";
            }
        };

        if (!passwordEncoder.matches(password, encodedPassword)) {
            logger.error("❌ Invalid password attempt for [{}]", username);
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
            return new ModelAndView("login", "error", "Invalid password.");
        }

        logger.info("🔐 Authentication success → Username: [{}], Role: [{}]", username, actualUserType);

        String role = "ROLE_" + actualUserType;
        User authUser = new User(username, encodedPassword,
                Collections.singletonList(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        loginAttempts.remove(username);
        lastLoginTime.put(username, LocalDateTime.now());

        logger.info("🎯 Redirecting [{}] to {} dashboard", username, actualUserType);

        return switch (actualUserType) {
            case "SUPERADMIN" -> new ModelAndView("redirect:/dashboard");
            case "ADMIN"      -> new ModelAndView("redirect:/dashboard_admin");
            case "DEALER"     -> new ModelAndView("redirect:/dashboard_dealer");
            case "USER"       -> new ModelAndView("redirect:/dashboard_user");
            case "CLIENT"     -> new ModelAndView("redirect:/dashboard_client");
            default           -> new ModelAndView("redirect:/login");
        };
    }
}
