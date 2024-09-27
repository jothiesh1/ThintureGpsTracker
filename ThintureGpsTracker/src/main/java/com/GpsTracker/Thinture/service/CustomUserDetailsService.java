package com.GpsTracker.Thinture.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private UserRepository userRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        // Add logic for Admin login FIRST
        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByEmailIgnoreCase(username));
        if (admin.isPresent()) {
            logger.info("Admin found with email: {}", username);
            Admin adm = admin.get();
            return new CustomUserDetails(adm);
        }

        // Fetch SuperAdmin by email NEXT
        Optional<SuperAdmin> superAdmin = Optional.ofNullable(superAdminRepository.findByEmail(username));
        if (superAdmin.isPresent()) {
            SuperAdmin spradmin = superAdmin.get();
            logger.info("SuperAdmin found with email: {}", username);
            return new CustomUserDetails(spradmin);
        }

        // Add logic for Dealer login (if needed)
        Optional<Dealer> dealer = Optional.ofNullable(dealerRepository.findByEmail(username));
        if (dealer.isPresent()) {
            Dealer deal = dealer.get();
            logger.info("Dealer found with email: {}", username);
            return new CustomUserDetails(deal);
        }

        // If no entity was found, throw exception
        logger.error("User not found with email: {}", username);
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}
    
    
    
/*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        // Fetch SuperAdmin by email
        Optional<SuperAdmin> superAdmin = Optional.ofNullable(superAdminRepository.findByEmail(username));
        if (superAdmin.isPresent()) {
            SuperAdmin admin = superAdmin.get();
            logger.info("SuperAdmin found with email: {}", username);
            return new CustomUserDetails(admin);
        }
        
        // Add similar logic for Admin, Dealer, User with logging
     // Add logic for Admin login
        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByEmailIgnoreCase(username));
        if (admin.isPresent()) {
            logger.info("Admin found with email: {}", username);
            return new CustomUserDetails(admin.get());
        } else {
            logger.error("Admin not found with email: {}", username);
            throw new UsernameNotFoundException("Admin not found with email: " + username);
        }}}
        // Add logic for Dealer login
		/*
		 * Optional<Dealer> dealer =
		 * Optional.ofNullable(dealerRepository.findByEmail(username)); if
		 * (dealer.isPresent()) { logger.info("Dealer found with email: {}", username);
		 * return new CustomUserDetails(dealer.get()); }
		 * 
		 * // Add logic for User login Optional<User> user =
		 * Optional.ofNullable(userRepository.findByEmail(username)); if
		 * (user.isPresent()) { logger.info("User found with email: {}", username);
		 * return new CustomUserDetails(user.get()); }
		 * 
		 * // If no user is found, log an error and throw an exception
		 * logger.error("User not found with username: {}", username); throw new
		 * UsernameNotFoundException("User not found with username: " + username); }}
		 * 
		 */