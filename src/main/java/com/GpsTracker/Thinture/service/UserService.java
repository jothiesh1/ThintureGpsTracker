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
import org.springframework.stereotype.Service;

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
    
    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.getId() == null) {
            throw new IllegalStateException("Logged-in user ID is null. Cannot assign ownership.");
        }

        logger.info("🔍 Logged-in User ID: {}", userDetails.getId());
        logger.info("🔍 Logged-in Role(s): {}", userDetails.getAuthorities());

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Admin admin = adminRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            user.setAdmin(admin);
            user.setAdmin_id(admin.getId());
            logger.info("✅ Setting admin_id: {}", admin.getId());

        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DEALER"))) {
            Dealer dealer = dealerRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Dealer not found"));
            user.setDealer(dealer);
            user.setDealer_id(dealer.getId());
            logger.info("✅ Setting dealer_id: {}", dealer.getId());

        

        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))) {
            Client client = clientRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            user.setClient(client);
            user.setClient_id(client.getId());
            logger.info("✅ Setting client_id: {}", client.getId());
        }

        logger.info("📥 Final User Object Before Save:");
        logger.info("➡ ID: {}", user.getId());
        logger.info("➡ Username: {}", user.getUsername());
        logger.info("➡ Admin ID: {}", user.getAdmin_id());
        logger.info("➡ Dealer ID: {}", user.getDealer_id());
      //  logger.info("➡ SuperAdmin ID: {}", user.getSuperadmin_id());
        logger.info("➡ Client ID: {}", user.getClient_id());

        return userRepository.save(user);
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
