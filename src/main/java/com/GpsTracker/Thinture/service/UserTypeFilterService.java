package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTypeFilterService {

    private static final Logger logger = LoggerFactory.getLogger(UserTypeFilterService.class);

    @Autowired private SuperAdminRepository superAdminRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private DealerRepository dealerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClientRepository clientRepository;

    public record UserTypeResult(Object userObject, String actualUserType, Long userId) {}

    
    
    /**
     * 🔍 Get creator name for User/Client (Dealer/Admin/SuperAdmin)
     */

    /**
     * 🔍 Get creator name for User/Client (Dealer/Admin/SuperAdmin)
     */
    
    @Transactional
    public String getCreatorName(Long userId, String role) {
        switch (role.toUpperCase()) {
            case "USER" -> {
                Optional<User> userOpt = userRepository.findById(userId);
                return userOpt.map(u -> {
                    if (u.getDealer() != null) return u.getDealer().getCompanyName();
                    if (u.getClient() != null) return u.getClient().getCompanyName();
                    return "N/A";
                }).orElse("N/A");
            }
            case "CLIENT" -> {
                Optional<Client> clientOpt = clientRepository.findById(userId);
                return clientOpt.map(c -> {
                    if (c.getDealer() != null) return c.getDealer().getCompanyName();
                    if (c.getAdmin() != null) return c.getAdmin().getCompanyName();
                    //if (c.getSuperadmin() != null) return c.getSuperadmin().();
                    return "N/A";
                }).orElse("N/A");
            }
            default -> {
                return "N/A";
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    public UserTypeResult findUserAndTypeByEmail(String email) {
    	 logger.info("🔍 [UserTypeFilterService] Checking user type for: {}", email);

        if (email == null || email.isBlank()) {
            logger.warn("⚠️ Email is empty or null. Aborting lookup.");
            return null;
        }

        SuperAdmin superAdmin = superAdminRepository.findByEmail(email);
        if (superAdmin != null) {
            logger.info("🟢 Match: SuperAdmin | Email: {}", email);
            return new UserTypeResult(superAdmin, "SUPERADMIN", superAdmin.getId());
        }

        Admin admin = adminRepository.findByEmailIgnoreCase(email);
        if (admin != null) {
            logger.info("🟢 Match: Admin | Email: {}", email);
            return new UserTypeResult(admin, "ADMIN", admin.getId());
        }

        Dealer dealer = dealerRepository.findByEmailIgnoreCase(email);
        if (dealer != null) {
            logger.info("🟢 Match: Dealer | Email: {}", email);
            return new UserTypeResult(dealer, "DEALER", dealer.getId());
        }

        com.GpsTracker.Thinture.model.User user = userRepository.findByEmail(email);
        if (user != null) {
            logger.info("🟢 Match: User | Email: {}", email);
            return new UserTypeResult(user, "USER", user.getId());
        }

        Client client = clientRepository.findByEmail(email);
        if (client != null) {
            logger.info("🟢 Match: Client | Email: {}", email);
            return new UserTypeResult(client, "CLIENT", client.getId());
        }

        logger.warn("❌ No user found in any repository for email: {}", email);
        return null;
    }

	
    
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

        com.GpsTracker.Thinture.model.User user = userRepository.findByResetToken(token);
        if (user != null) return user;

        return null;
    }

  

    
}
