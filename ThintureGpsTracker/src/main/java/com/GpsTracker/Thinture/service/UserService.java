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
        // Get the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.getId() == null) {
            throw new IllegalStateException("Logged-in user ID is null. Cannot assign ownership.");
        }

        // Assign the logged-in user's ID to the new user
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
            user.setAdmin(admin);
            
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DEALER"))) {
            Dealer dealer = dealerRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("Dealer not found"));
            user.setDealer(dealer);
            
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
            SuperAdmin superAdmin = superAdminRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("SuperAdmin not found"));
            user.setSuperAdmin(superAdmin);
            
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))) {
            Client client = clientRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("Client not found"));
            user.setClient(client);
        }

        logger.info("User is being added by User ID: {}", userDetails.getId());
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


    // Add any other business logic or methods as needed
}
