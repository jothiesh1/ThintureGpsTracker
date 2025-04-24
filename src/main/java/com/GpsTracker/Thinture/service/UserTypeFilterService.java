package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserTypeFilterService {

    private static final Logger logger = LoggerFactory.getLogger(UserTypeFilterService.class);

    @Autowired private SuperAdminRepository superAdminRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private DealerRepository dealerRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;

    /**
     * ✅ Final UserTypeResult used throughout the app
     */
    public record UserTypeResult(
        Object userObject,
        String actualUserType,
        Long id,
        Long superadminId,
        Long adminId,
        Long dealerId,
        Long clientId,
        Long userId
    ) {
        public String getRole() {
            return actualUserType;
        }

        public Long getId() {
            return id;
        }

        public Long getSuperadminId() {
            return superadminId;
        }

        public Long getAdminId() {
            return adminId;
        }

        public Long getDealerId() {
            return dealerId;
        }

        public Long getClientId() {
            return clientId;
        }

        public Long getUserId() {
            return userId;
        }
    }

    /**
     * 🔍 Identify the logged-in user's type and all role hierarchy IDs
     */
    public UserTypeResult findUserAndTypeByEmail(String email) {
        logger.info("🔍 Checking user type for: {}", email);

        if (email == null || email.isBlank()) {
            logger.warn("⚠️ Email is blank. Aborting role check.");
            return null;
        }

        SuperAdmin superAdmin = superAdminRepository.findByEmail(email);
        if (superAdmin != null) {
            logger.info("✅ Found SuperAdmin: {}", email);
            return new UserTypeResult(superAdmin, "SUPERADMIN",
                    superAdmin.getId(),
                    superAdmin.getId(), null, null, null, null);
        }

        Admin admin = adminRepository.findByEmailIgnoreCase(email);
        if (admin != null) {
            logger.info("✅ Found Admin: {}", email);
            return new UserTypeResult(admin, "ADMIN",
                    admin.getId(),
                    admin.getSuperadmin_id(), admin.getId(), null, null, null);
        }

        Dealer dealer = dealerRepository.findByEmailIgnoreCase(email);
        if (dealer != null) {
            logger.info("✅ Found Dealer: {}", email);
            return new UserTypeResult(dealer, "DEALER",
                    dealer.getId(),
                    dealer.getSuperadmin_id(),
                    dealer.getAdmin_id(),
                    dealer.getId(), null, null);
        }

        Client client = clientRepository.findByEmail(email);
        if (client != null) {
            logger.info("✅ Found Client: {}", email);
            return new UserTypeResult(client, "CLIENT",
                    client.getId(),
                    client.getSuperadmin_id(),
                    client.getAdmin_id(),
                    client.getDealer_id(),
                    client.getId(), null);
        }

        User user = userRepository.findByEmail(email);
        if (user != null) {
            logger.info("✅ Found User: {}", email);
            return new UserTypeResult(user, "USER",
                    user.getId(),
                    user.getSuperadmin_id(),
                    user.getAdmin_id(),
                    user.getDealer_id(),
                    user.getClient_id(),
                    user.getId());
        }

        logger.warn("❌ No user found for: {}", email);
        return null;
    }

    /**
     * 🔁 Used in SupportTicket form to get creator company name
     */
    @Transactional
    public String getCreatorName(Long userId, String role) {
        logger.debug("📌 Getting creator name for ID: {}, Role: {}", userId, role);

        return switch (role.toUpperCase()) {
            case "USER" -> {
                Optional<User> user = userRepository.findById(userId);
                yield user.map(u -> {
                    if (u.getClient() != null) return u.getClient().getCompanyName();
                    if (u.getDealer() != null) return u.getDealer().getCompanyName();
                    return "N/A";
                }).orElse("N/A");
            }
            case "CLIENT" -> {
                Optional<Client> client = clientRepository.findById(userId);
                yield client.map(c -> {
                    if (c.getDealer() != null) return c.getDealer().getCompanyName();
                    if (c.getAdmin() != null) return c.getAdmin().getCompanyName();
                    return "N/A";
                }).orElse("N/A");
            }
            default -> "N/A";
        };
    }

    /**
     * Optional - used for Forgot Password / Reset Token
     */
    public Object findUserByResetToken(String token) {
        if (token == null || token.isBlank()) return null;

        Admin admin = adminRepository.findByResetToken(token);
        if (admin != null) return admin;

        Dealer dealer = dealerRepository.findByResetToken(token);
        if (dealer != null) return dealer;

        SuperAdmin superAdmin = superAdminRepository.findByResetToken(token);
        if (superAdmin != null) return superAdmin;

        Client client = clientRepository.findByResetToken(token);
        if (client != null) return client;

        User user = userRepository.findByResetToken(token);
        return user;
    }
}
