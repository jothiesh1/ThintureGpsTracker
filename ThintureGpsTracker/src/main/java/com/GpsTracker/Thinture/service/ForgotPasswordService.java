package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;



@Service
public class ForgotPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    @Autowired private UserTypeFilterService filterService;
    @Autowired private SuperAdminRepository superAdminRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private DealerRepository dealerRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    
    
    
    
    @Transactional
    public void initiateReset(String email, HttpServletRequest request) {
        logger.info("📨 Forgot password requested for: {}", email);
        logger.debug("🔍 Looking up user by email: {}", email);

        UserTypeResult result = filterService.findUserAndTypeByEmail(email);

        if (result != null) {
            String token = UUID.randomUUID().toString();
            Object user = result.userObject();
            String userType = result.actualUserType();

            logger.info("✅ User found - Type: {}, ID: {}", userType, result.userId());
            logger.debug("🔑 Reset token generated: {}", token);

            switch (userType) {
                case "ADMIN" -> {
                    Admin admin = (Admin) user;
                    admin.setResetToken(token);
                    adminRepo.save(admin);
                    logger.debug("💾 Token saved for ADMIN ID: {}", admin.getId());
                }
                case "DEALER" -> {
                    Dealer dealer = (Dealer) user;
                    dealer.setResetToken(token);
                    dealerRepo.save(dealer);
                    logger.debug("💾 Token saved for DEALER ID: {}", dealer.getId());
                }
                case "SUPERADMIN" -> {
                    SuperAdmin sa = (SuperAdmin) user;
                    sa.setResetToken(token);
                    superAdminRepo.save(sa);
                    logger.debug("💾 Token saved for SUPERADMIN ID: {}", sa.getId());
                }
                case "CLIENT" -> {
                    Client client = (Client) user;
                    client.setResetToken(token);
                    clientRepo.save(client);
                    logger.debug("💾 Token saved for CLIENT ID: {}", client.getId());
                }
                case "USER" -> {
                    com.GpsTracker.Thinture.model.User u = (com.GpsTracker.Thinture.model.User) user;
                    u.setResetToken(token);
                    userRepo.save(u);
                    logger.debug("💾 Token saved for USER ID: {}", u.getId());
                }
                default -> logger.error("❌ Unknown user type encountered: {}", userType);
            }

            String resetUrl = request.getRequestURL().toString().replace("/forgot_password", "/reset_password?token=" + token);
            logger.info("🔗 Reset password link generated: {}", resetUrl);

            try {
                logger.debug("✉️ Preparing to send reset email to {}", email);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("🔐 Thinture GPS - Reset Your Password");
                message.setText("Hello,\n\nClick the link below to reset your password:\n" + resetUrl +
                        "\n\nIf you did not request this, please ignore this message.\n\n— Thinture GPS Team");

                mailSender.send(message);
                logger.info("✅ Reset email sent to: {}", email);
            } catch (Exception e) {
                logger.error("❌ Failed to send reset email to {}. Error: {}", email, e.getMessage(), e);
            }

        } else {
            logger.warn("⚠️ No user found for provided email: {}", email);
        }
    }


    
    
    
    
    

    public boolean resetPassword(String token, String newPassword) {
        logger.info("🔄 Processing password reset for token: {}", token);
        Object user = filterService.findUserByResetToken(token);

        if (user != null) {
            String encoded = passwordEncoder.encode(newPassword);

            if (user instanceof Admin a) {
                a.setPassword(encoded);
                a.setResetToken(null);
                adminRepo.save(a);
                logger.info("✅ Password reset for Admin ID: {}", a.getId());
            } else if (user instanceof Dealer d) {
                d.setPassword(encoded);
                d.setResetToken(null);
                dealerRepo.save(d);
                logger.info("✅ Password reset for Dealer ID: {}", d.getId());
            } else if (user instanceof SuperAdmin s) {
                s.setPassword(encoded);
                s.setResetToken(null);
                superAdminRepo.save(s);
                logger.info("✅ Password reset for SuperAdmin ID: {}", s.getId());
            } else if (user instanceof Client c) {
                c.setPassword(encoded);
                c.setResetToken(null);
                clientRepo.save(c);
                logger.info("✅ Password reset for Client ID: {}", c.getId());
            } else if (user instanceof com.GpsTracker.Thinture.model.User u) {
                u.setPassword(encoded);
                u.setResetToken(null);
                userRepo.save(u);
                logger.info("✅ Password reset for User ID: {}", u.getId());
            }
            return true;
        }

        logger.error("❌ Invalid or expired reset token: {}", token);
        return false;
    }
}