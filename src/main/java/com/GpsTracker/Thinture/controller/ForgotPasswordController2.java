package com.GpsTracker.Thinture.controller;





import com.GpsTracker.Thinture.service.ForgotPasswordService;

//===========================
//1. CONTROLLER - ForgotPasswordController.java
//===========================

import com.GpsTracker.Thinture.service.UserService2;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import com.GpsTracker.Thinture.service.UserTypeFilterService;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;
import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class ForgotPasswordController2 {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController2.class);

    @Autowired private UserTypeFilterService filterService;
    @Autowired private SuperAdminRepository superAdminRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private DealerRepository dealerRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    
    
    @Autowired private ForgotPasswordService forgotPasswordService; // ✅ Add this

    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        logger.info("📨 Forgot password request received for email: {}", email);
        try {
            forgotPasswordService.initiateReset(email, request); // ✅ Delegating to service
            redirectAttributes.addFlashAttribute("message", "Reset link has been sent to your email.");
        } catch (Exception e) {
            logger.error("❌ Forgot password process failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to process password reset.");
        }
        return "redirect:/forgot_password";
    }
    
    
    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam String token, Model model) {
        logger.debug("📝 Showing reset password form for token: {}", token);
        model.addAttribute("token", token);
        return "reset_password";
    }

    
    
    @PostMapping("/reset_password")
    public String processReset(@RequestParam String token, @RequestParam String newPassword) {
        logger.debug("🔐 Attempting to reset password with token: {}", token);

        Object user = filterService.findUserByResetToken(token);

        if (user != null) {
            String encoded = passwordEncoder.encode(newPassword);

            if (user instanceof Admin a) {
                a.setPassword(encoded);
                a.setResetToken(null);
                adminRepo.save(a);
                logger.info("✅ Password reset for ADMIN ID: {}", a.getId());
            } else if (user instanceof Dealer d) {
                d.setPassword(encoded);
                d.setResetToken(null);
                dealerRepo.save(d);
                logger.info("✅ Password reset for DEALER ID: {}", d.getId());
            } else if (user instanceof SuperAdmin s) {
                s.setPassword(encoded);
                s.setResetToken(null);
                superAdminRepo.save(s);
                logger.info("✅ Password reset for SUPERADMIN ID: {}", s.getId());
            } else if (user instanceof Client c) {
                c.setPassword(encoded);
                c.setResetToken(null);
                clientRepo.save(c);
                logger.info("✅ Password reset for CLIENT ID: {}", c.getId());
            } else if (user instanceof com.GpsTracker.Thinture.model.User u) {
                u.setPassword(encoded);
                u.setResetToken(null);
                userRepo.save(u);
                logger.info("✅ Password reset for USER ID: {}", u.getId());
            }

            return "redirect:/login?resetSuccess";
        }

        logger.error("❌ Invalid or expired reset token: {}", token);
        return "redirect:/reset_password?invalid";
    }
}