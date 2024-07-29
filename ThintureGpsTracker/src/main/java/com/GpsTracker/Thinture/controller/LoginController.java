package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // Path to login.html
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password) {
        logger.info("Attempting to login with username: {}", username);

        // Fetch user from database
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            logger.info("Login successful for username: {}", username);
            return new ModelAndView("redirect:/dashboard");
        } else {
            logger.error("Invalid login attempt for username: {}", username);
            return new ModelAndView("login", "error", "Invalid username or password.");
        }
    }}

