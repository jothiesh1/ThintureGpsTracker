package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;
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
}
