package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.service.UserService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class ForgotPasswordController2 {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController2.class);
    private final UserService2 userService;

    public ForgotPasswordController2(UserService2 userService) {
        this.userService = userService;
    }

    // ✅ Step 1: Forgot Password API (Generate Token & Send Email)
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("Received forgot password request for email: {}", email);

        Map<String, String> response = new HashMap<>();

        try {
            boolean isEmailSent = userService.sendPasswordResetLink(email);

            if (isEmailSent) {
                response.put("message", "A password reset link has been sent to your email.");
                logger.info("Reset link sent successfully to {}", email);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Email not found in our system.");
                logger.warn("Failed password reset attempt: Email {} not found", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            logger.error("Error processing forgot password request: {}", e.getMessage());
            response.put("message", "Internal server error. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // ✅ Step 2: Serve Reset Password Page (HTML)
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password"; // Must match reset_password.html in resources/templates
    }

    // ✅ Step 3: Handle Password Reset
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        logger.info("Processing password reset request for token: {}", token);

        boolean isReset = userService.resetPassword(token, newPassword);
        Map<String, String> response = new HashMap<>();

        if (isReset) {
            response.put("message", "Your password has been reset successfully.");
            logger.info("Password reset successful for token: {}", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid or expired token.");
            logger.warn("Password reset failed: Invalid token {}", token);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
