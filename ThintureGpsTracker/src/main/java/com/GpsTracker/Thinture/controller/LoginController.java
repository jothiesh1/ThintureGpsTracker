package com.GpsTracker.Thinture.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Collections;
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

   @GetMapping("/login")
   public String showLoginPage() {
       logger.info("Rendering login page.");
       return "login";  // Return login.html
   }

   @PostMapping("/login")
   public ModelAndView login(@RequestParam String usertype,
                             @RequestParam String username,
                             @RequestParam String password) {
       logger.info("Login attempt for username: {} with usertype: {}", username, usertype);

       Object user = null;
       String role = null;
       Long userId = null;

       // Fetch user by role type and get the ID
       switch (usertype.toUpperCase()) {
           case "SUPERADMIN":
               logger.debug("Checking SuperAdmin repository for username: {}", username);
               user = superAdminRepository.findByEmail(username);  // Fetch by email only
               if (user != null && user instanceof SuperAdmin) {
                   userId = ((SuperAdmin) user).getId();  // Fetch SuperAdmin ID
                   logger.info("SuperAdmin login successful. SuperAdmin ID: {}", userId);
               } else {
                   logger.error("SuperAdmin login failed. User not found for username: {}", username);
                   return new ModelAndView("login", "error", "Invalid username or password.");
               }
               role = "ROLE_SUPERADMIN";
               break;

           case "ADMIN":
               logger.debug("Checking Admin repository for username: {}", username);
               user = adminRepository.findByEmailIgnoreCase(username);  // Fetch by email only
               if (user != null && user instanceof Admin) {
                   userId = ((Admin) user).getId();  // Fetch Admin ID
                   logger.info("Admin login successful. Admin ID: {}", userId);
               } else {
                   logger.error("Admin login failed. User not found for username: {}", username);
                   return new ModelAndView("login", "error", "Invalid username or password.");
               }
               role = "ROLE_ADMIN";
               break;

           case "DEALER":
               logger.debug("Checking Dealer repository for username: {}", username);
               user = dealerRepository.findByEmail(username);  // Fetch by email only
               if (user == null) {
                   logger.error("Dealer login failed. User not found for username: {}", username);
                   return new ModelAndView("login", "error", "Invalid username or password.");
               }
               role = "ROLE_DEALER";
               break;

           case "USER":
               logger.debug("Checking User repository for username: {}", username);
               user = userRepository.findByEmail(username);  // Fetch by email only
               if (user == null) {
                   logger.error("User login failed. User not found for username: {}", username);
                   return new ModelAndView("login", "error", "Invalid username or password.");
               }
               role = "ROLE_USER";
               break;

           default:
               logger.error("Invalid user type: {}", usertype);
               return new ModelAndView("login", "error", "Invalid user type.");
       }

       // Validate password for each user type
       if (user != null) {
           boolean passwordMatches = false;
           String encodedPassword = "";

           if (user instanceof SuperAdmin) {
               encodedPassword = ((SuperAdmin) user).getPassword();
           } else if (user instanceof Admin) {
               encodedPassword = ((Admin) user).getPassword();
           } else if (user instanceof Dealer) {
               encodedPassword = ((Dealer) user).getPassword();
           } else if (user instanceof User) {
               encodedPassword = ((User) user).getPassword();
           }

           // Check if password matches
           passwordMatches = passwordEncoder.matches(password, encodedPassword);
           logger.info("Password matching result for username {}: {}", username, passwordMatches);

           if (passwordMatches) {
               logger.info("Login successful for username: {} with usertype: {}", username, usertype);

               // Set authentication and role
               org.springframework.security.core.userdetails.User authenticatedUser =
                       new org.springframework.security.core.userdetails.User(username, encodedPassword,
                               Collections.singletonList(new SimpleGrantedAuthority(role)));

               SecurityContextHolder.getContext().setAuthentication(
                       new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities())
               );

               // Redirect based on role
               switch (usertype.toUpperCase()) {
                   case "SUPERADMIN":
                       logger.info("Redirecting SuperAdmin to /dashboard");
                       return new ModelAndView("redirect:/dashboard");
                   case "ADMIN":
                       logger.info("Redirecting Admin to /admin/dashboard");
                       return new ModelAndView("redirect:/dashboard");
                   case "DEALER":
                       logger.info("Redirecting Dealer to /dealers/add");
                       return new ModelAndView("redirect:/dealers/add");
                   case "USER":
                       logger.info("Redirecting User to /map");
                       return new ModelAndView("redirect:/map");
                   default:
                       logger.warn("Usertype not found, redirecting to /dashboard");
                       return new ModelAndView("redirect:/dashboard");
               }
           } else {
               logger.error("Invalid password for username: {}", username);
               return new ModelAndView("login", "error", "Invalid password.");
           }
       } else {
           logger.error("Login failed for username: {}", username);
           return new ModelAndView("login", "error", "Invalid username or password.");
       }
   }



    @GetMapping("/default")
    public String defaultAfterLogin() {
        logger.info("Redirecting to default dashboard after successful login.");
        return "redirect:/dashboard";
    }
}
