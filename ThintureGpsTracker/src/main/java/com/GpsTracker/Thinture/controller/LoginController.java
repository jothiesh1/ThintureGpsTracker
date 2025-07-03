package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
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
        logger.info("Rendering login page.");
        return "login";
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String usertype,
                              HttpServletRequest request,
                              HttpServletResponse response) {   // ✅ Added HttpServletResponse
        logger.info("🔐 Login attempt - Username: {}, Selected Type: {}", username, usertype);

        if (loginAttempts.getOrDefault(username, 0) >= 5) {
            logger.warn("⛔ Account locked for user: {}", username);
            return new ModelAndView("login", "error", "Account locked due to too many failed attempts.");
        }

        var result = userTypeFilterService.findUserAndTypeByEmail(username);
        if (result == null) {
            logger.error("❌ Login failed - User not found: {}", username);
            return new ModelAndView("login", "error", "Invalid username or password.");
        }

        Object userObj = result.userObject();
        String actualUserType = result.actualUserType();
        Long userId = result.userId();

        logger.info("🔎 Auto-detected usertype: {} for {}", actualUserType, username);

        if (!usertype.equalsIgnoreCase(actualUserType)) {
            logger.warn("🚫 UserType mismatch! Selected: {}, Actual: {}", usertype, actualUserType);
            return new ModelAndView("login", "error", "Selected user type is incorrect for this account.");
        }

        boolean isActive = switch (actualUserType) {
            case "SUPERADMIN" -> ((SuperAdmin) userObj).isStatus();
            case "ADMIN" -> ((Admin) userObj).isStatus();
            case "DEALER" -> ((Dealer) userObj).isStatus();
            case "CLIENT" -> ((Client) userObj).isStatus();
            case "USER" -> ((com.GpsTracker.Thinture.model.User) userObj).isStatus();
            default -> false;
        };

        if (!isActive) {
            logger.warn("⛔ Login blocked - Inactive account for user: {}", username);
            return new ModelAndView("login", "error", "Your account is inactive. Please contact your Admin or Dealer.");
        }

        String encodedPassword = switch (actualUserType) {
            case "SUPERADMIN" -> ((SuperAdmin) userObj).getPassword();
            case "ADMIN" -> ((Admin) userObj).getPassword();
            case "DEALER" -> ((Dealer) userObj).getPassword();
            case "CLIENT" -> ((Client) userObj).getPassword();
            case "USER" -> ((com.GpsTracker.Thinture.model.User) userObj).getPassword();
            default -> "";
        };

        if (!passwordEncoder.matches(password, encodedPassword)) {
            logger.error("❌ Invalid password for user: {}", username);
            loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
            return new ModelAndView("login", "error", "Invalid password.");
        }

        logger.info("✅ Login successful - Username: {}, Role: {}, ID: {}", username, actualUserType, userId);

        // ✅ Set userType cookie (clear old one if exists)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userType".equals(cookie.getName())) {
                    cookie.setMaxAge(0);  // delete old cookie
                    cookie.setPath("/");  // important: match the same path
                    response.addCookie(cookie);
                    logger.info("🧹 Deleted old userType cookie.");
                }
            }
        }

        Cookie userTypeCookie = new Cookie("userType", actualUserType);
        userTypeCookie.setMaxAge(60 * 60); // 1 hour
        userTypeCookie.setPath("/");       // ✅ must be '/'
        response.addCookie(userTypeCookie);
        logger.info("🍪 Set userType cookie: {}", actualUserType);

        CustomUserDetails customUserDetails = switch (actualUserType) {
            case "SUPERADMIN" -> new CustomUserDetails((SuperAdmin) userObj);
            case "ADMIN" -> new CustomUserDetails((Admin) userObj);
            case "DEALER" -> new CustomUserDetails((Dealer) userObj);
            case "CLIENT" -> new CustomUserDetails((Client) userObj);
            case "USER" -> new CustomUserDetails((com.GpsTracker.Thinture.model.User) userObj);
            default -> throw new RuntimeException("Unsupported user type: " + actualUserType);
        };

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        loginAttempts.remove(username);
        lastLoginTime.put(username, LocalDateTime.now());

        return switch (actualUserType) {
            case "SUPERADMIN" -> new ModelAndView("redirect:/dashboard");
            case "ADMIN" -> new ModelAndView("redirect:/admin/dashboard");
            case "DEALER" -> new ModelAndView("redirect:/dealer/dashboard");
            case "USER" -> new ModelAndView("redirect:/user/dashboard_user");
            case "CLIENT" -> new ModelAndView("redirect:/client/dashboard_client");
            default -> new ModelAndView("redirect:/login");
        };
    }
}
