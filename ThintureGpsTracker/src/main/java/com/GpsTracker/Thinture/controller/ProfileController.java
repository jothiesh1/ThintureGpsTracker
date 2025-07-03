package com.GpsTracker.Thinture.controller;

import java.util.HashMap;
import java.util.Map;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.security.AuthenticationFacade;
import com.GpsTracker.Thinture.security.CustomUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        CustomUserDetails userDetails = authenticationFacade.getAuthenticatedUserDetails();
        if (userDetails == null) {
            logger.warn("⚠️ No user is currently authenticated.");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        logger.info("🔍 Fetching profile for user: {} with role: {}", userDetails.getUsername(), userDetails.getAuthorities());

        Map<String, Object> profile = new HashMap<>();

        switch (userDetails.getAuthorities().iterator().next().getAuthority()) {
            case "ROLE_SUPERADMIN":
                SuperAdmin sa = superAdminRepository.findById(userDetails.getId()).orElse(null);
                if (sa != null) {
                    logger.info("✅ Found SuperAdmin: {}", sa.getEmail());
                    profile.put("companyName", sa.getName());
                    profile.put("email", sa.getEmail());
                    //profile.put("address", sa.getAddress());
                    profile.put("phone", sa.getPhone());
                    profile.put("country", sa.getCountry());
                    // profile.put("photo", sa.getPhotoUrl());  // ❌ COMMENTED (no photo field yet)
                }
                break;

            case "ROLE_ADMIN":
                Admin admin = adminRepository.findById(userDetails.getId()).orElse(null);
                if (admin != null) {
                    logger.info("✅ Found Admin: {}", admin.getEmail());
                    profile.put("companyName", admin.getCompanyName());
                    profile.put("email", admin.getEmail());
                    profile.put("address", admin.getAddress());
                    profile.put("phone", admin.getPhone());
                    profile.put("country", admin.getCountry());
                    // profile.put("photo", admin.getPhotoUrl());  // ❌ COMMENTED
                }
                break;

            case "ROLE_DEALER":
                Dealer dealer = dealerRepository.findById(userDetails.getId()).orElse(null);
                if (dealer != null) {
                    logger.info("✅ Found Dealer: {}", dealer.getEmail());
                    profile.put("companyName", dealer.getCompanyName());
                    profile.put("email", dealer.getEmail());
                    profile.put("address", dealer.getAddress());
                    profile.put("phone", dealer.getPhone());
                    profile.put("country", dealer.getCountry());
                    // profile.put("photo", dealer.getPhotoUrl());  // ❌ COMMENTED
                }
                break;

            case "ROLE_CLIENT":
                Client client = clientRepository.findById(userDetails.getId()).orElse(null);
                if (client != null) {
                    logger.info("✅ Found Client: {}", client.getEmail());
                    profile.put("companyName", client.getCompanyName());
                    profile.put("email", client.getEmail());
                    profile.put("address", client.getAddress());
                    profile.put("phone", client.getPhone());
                    profile.put("country", client.getCountry());
                    // profile.put("photo", client.getPhotoUrl());  // ❌ COMMENTED
                }
                break;

            case "ROLE_USER":
                User user = userRepository.findById(userDetails.getId()).orElse(null);
                if (user != null) {
                    logger.info("✅ Found User: {}", user.getEmail());
                    profile.put("companyName", user.getCompanyName());
                    profile.put("email", user.getEmail());
                    profile.put("address", user.getAddress());
                    profile.put("phone", user.getPhone());
                    profile.put("country", user.getCountry());
                    // profile.put("photo", user.getPhotoUrl());  // ❌ COMMENTED
                }
                break;

            default:
                logger.warn("⚠️ Unknown role: {}", userDetails.getAuthorities());
                return ResponseEntity.badRequest().body("Invalid user role");
        }

        return ResponseEntity.ok(profile);
    }
}
