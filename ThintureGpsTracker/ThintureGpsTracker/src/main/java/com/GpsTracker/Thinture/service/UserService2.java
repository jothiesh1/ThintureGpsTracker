package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class UserService2 {

    private static final Logger logger = LoggerFactory.getLogger(UserService2.class);

    @Autowired
    private SuperAdminRepository superAdminRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailNotificationService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder; // Injected PasswordEncoder

    // ✅ Send Password Reset Link
    public boolean sendPasswordResetLink(String email) {
        logger.info("Looking for user with email: {}", email);

        try {
            Object user = findUserByEmail(email);

            // 🔹 Fix: Even if the email is not found, return true (prevents email enumeration attacks)
            if (user == null) {
                logger.warn("User not found with email: {}. Returning success response to prevent enumeration.", email);
                return true;
            }

            // Generate reset token
            String token = UUID.randomUUID().toString();
            logger.info("Generated reset token for {}: {}", email, token);

            // Save reset token
            saveResetToken(user, token);

            // Send password reset email
            emailService.sendPasswordResetEmail(email, token);
            logger.info("Password reset email sent to {}", email);
            return true;

        } catch (Exception e) {
            logger.error("Error in sending password reset link: {}", e.getMessage(), e);
            return false;
        }
    }

    // ✅ Find User by Email (Checks across all user tables)
    private Object findUserByEmail(String email) {
        logger.info("Searching for user by email: {}", email);

        Object user = superAdminRepository.findByEmailIgnoreCase(email);
        if (user != null) return user;

        user = adminRepository.findByEmailIgnoreCase(email);
        if (user != null) return user;

        user = clientRepository.findByEmailIgnoreCase(email);
        if (user != null) return user;

        user = dealerRepository.findByEmailIgnoreCase(email);
        if (user != null) return user;

        user = userRepository.findByEmailIgnoreCase(email);
        if (user != null) return user;

        logger.warn("No user found with email: {}", email);
        return null;
    }

    // ✅ Save Reset Token to the Correct User Table
    private void saveResetToken(Object user, String token) {
        try {
            if (user instanceof SuperAdmin) {
                ((SuperAdmin) user).setResetToken(token);
                superAdminRepository.save((SuperAdmin) user);
            } else if (user instanceof Admin) {
                ((Admin) user).setResetToken(token);
                adminRepository.save((Admin) user);
            } else if (user instanceof Client) {
                ((Client) user).setResetToken(token);
                clientRepository.save((Client) user);
            } else if (user instanceof Dealer) {
                ((Dealer) user).setResetToken(token);
                dealerRepository.save((Dealer) user);
            } else if (user instanceof User) {
                ((User) user).setResetToken(token);
                userRepository.save((User) user);
            }
            logger.info("Reset token successfully saved for {}", user.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error saving reset token for {}: {}", user.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    // ✅ Find User by Reset Token
    private Object findUserByToken(String token) {
        logger.info("Searching for user by reset token: {}", token);

        Object user = superAdminRepository.findByResetToken(token);
        if (user != null) return user;

        user = adminRepository.findByResetToken(token);
        if (user != null) return user;

        user = clientRepository.findByResetToken(token);
        if (user != null) return user;

        user = dealerRepository.findByResetToken(token);
        if (user != null) return user;

        user = userRepository.findByResetToken(token);
        if (user != null) return user;

        logger.warn("No user found with reset token: {}", token);
        return null;
    }

    // ✅ Reset User Password
    public boolean resetPassword(String token, String newPassword) {
        logger.info("Resetting password for token: {}", token);

        Object user = findUserByToken(token);
        if (user == null) {
            logger.warn("Invalid password reset token: {}", token);
            return false;
        }

        updateUserPassword(user, newPassword);
        logger.info("Password reset successful for token: {}", token);
        return true;
    }

    // ✅ Update User Password and Remove Token
    private void updateUserPassword(Object user, String newPassword) {
        try {
            String encodedPassword = passwordEncoder.encode(newPassword);

            if (user instanceof SuperAdmin) {
                ((SuperAdmin) user).setPassword(encodedPassword);
                ((SuperAdmin) user).setResetToken(null);
                superAdminRepository.save((SuperAdmin) user);
            } else if (user instanceof Admin) {
                ((Admin) user).setPassword(encodedPassword);
                ((Admin) user).setResetToken(null);
                adminRepository.save((Admin) user);
            } else if (user instanceof Client) {
                ((Client) user).setPassword(encodedPassword);
                ((Client) user).setResetToken(null);
                clientRepository.save((Client) user);
            } else if (user instanceof Dealer) {
                ((Dealer) user).setPassword(encodedPassword);
                ((Dealer) user).setResetToken(null);
                dealerRepository.save((Dealer) user);
            } else if (user instanceof User) {
                ((User) user).setPassword(encodedPassword);
                ((User) user).setResetToken(null);
                userRepository.save((User) user);
            }
            logger.info("Password updated successfully for {}", user.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
        }
    }
}
