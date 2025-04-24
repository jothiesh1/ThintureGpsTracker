package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
//import com.GpsTracker.Thinture.model.Userr;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;

import jakarta.transaction.Transactional;

import com.GpsTracker.Thinture.dto.RoleHierarchyDTO;
import com.GpsTracker.Thinture.Util.RoleHierarchyUtil;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTypeFilterService userTypeFilterService;

    @Transactional
    public User addUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();
        UserTypeResult creator = userTypeFilterService.findUserAndTypeByEmail(email);

        logger.info("📥 Add User by: {} (Role: {})", email, creator.getRole());

        RoleHierarchyDTO dto = RoleHierarchyUtil.prepareHierarchy(creator);

        // ✅ Apply hierarchy to user
        user.setSuperadmin_id(dto.getSuperadmin_id());
        user.setAdmin_id(dto.getAdmin_id());
        user.setDealer_id(dto.getDealer_id());
        user.setClient_id(dto.getClient_id());
        user.setUser_id(dto.getUser_id());

        // 🔐 Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info("📦 Final User Save Payload:");
        logger.info("➡ Email: {}", user.getEmail());
        logger.info("➡ Username: {}", user.getUsername());
        logger.info("➡ Superadmin ID: {}", user.getSuperadmin_id());
        logger.info("➡ Admin ID: {}", user.getAdmin_id());
        logger.info("➡ Dealer ID: {}", user.getDealer_id());
        logger.info("➡ Client ID: {}", user.getClient_id());

        User saved = userRepository.save(user);
        logger.info("✅ User saved with ID: {}", saved.getId());

        return saved;
    }
    


    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        logger.info("User deleted successfully.");
    }

    public void deleteUserr(Long id) {
        logger.info("Deleting userr with ID: {}", id);
        userRepository.deleteById(id);
        logger.info("Userr deleted successfully.");
    }

	
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
      
        return users;
    }



    // ✅ Update user details
    public void updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setCompanyName(updatedUser.getCompanyName());
        existing.setEmail(updatedUser.getEmail());
        existing.setAddress(updatedUser.getAddress());
        existing.setPhone(updatedUser.getPhone());
        existing.setCountry(updatedUser.getCountry());
        userRepository.save(existing);
    }



    /**
     * ✅ Toggle user's active status
     */
    public Optional<User> toggleStatus(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(user -> {
            user.setStatus(!user.isStatus());
            userRepository.save(user);
        });
        return optionalUser;
    }



    // Add any other business logic or methods as needed
}
