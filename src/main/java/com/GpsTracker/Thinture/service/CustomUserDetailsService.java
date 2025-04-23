package com.GpsTracker.Thinture.service;


import java.util.Collections;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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


import com.GpsTracker.Thinture.model.*;
import com.GpsTracker.Thinture.repository.*;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("🔹 Attempting to load user by username: {}", username);

        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByEmailIgnoreCase(username));
        if (admin.isPresent()) {
            logger.info("✅ Admin found: {}", username);
            return setAuthentication(new CustomUserDetails(admin.get()));
        }

        Optional<SuperAdmin> superAdmin = Optional.ofNullable(superAdminRepository.findByEmail(username));
        if (superAdmin.isPresent()) {
            logger.info("✅ SuperAdmin found: {}", username);
            return setAuthentication(new CustomUserDetails(superAdmin.get()));
        }

        Optional<Dealer> dealer = Optional.ofNullable(dealerRepository.findByEmail(username));
        if (dealer.isPresent()) {
            logger.info("✅ Dealer found: {}", username);
            return setAuthentication(new CustomUserDetails(dealer.get()));
        }

        Optional<com.GpsTracker.Thinture.model.User> user = Optional.ofNullable(userRepository.findByEmail(username));
        if (user.isPresent()) {
            logger.info("✅ User found: {}", username);
            return setAuthentication(new CustomUserDetails(user.get()));
        }

        Optional<Client> client = Optional.ofNullable(clientRepository.findByEmail(username));
        if (client.isPresent()) {
            logger.info("✅ Client found: {}", username);
            return setAuthentication(new CustomUserDetails(client.get()));
        }


        logger.error("❌ User not found with email: {}", username);
        throw new UsernameNotFoundException("User not found with email: " + username);
    }

    /**
     * ✅ Set authentication in SecurityContext to prevent "No authenticated user found" warning
     */
    private UserDetails setAuthentication(CustomUserDetails userDetails) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        logger.info("🔐 SecurityContext updated for user: {} | Role: {}", userDetails.getUsername(), userDetails.getAuthorities());
        return userDetails;
    }
}















/*


import java.util.Collections;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ClientRepository clientRepository;  // Added Client Repository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        // Check for Admin
        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByEmailIgnoreCase(username));
        if (admin.isPresent()) {
            logger.info("Admin found with email: {}", username);
            return new CustomUserDetails(admin.get());
        }

        // Check for SuperAdmin
        Optional<SuperAdmin> superAdmin = Optional.ofNullable(superAdminRepository.findByEmail(username));
        if (superAdmin.isPresent()) {
            logger.info("SuperAdmin found with email: {}", username);
            return new CustomUserDetails(superAdmin.get());
        }

        // Check for Dealer
        Optional<Dealer> dealer = Optional.ofNullable(dealerRepository.findByEmail(username));
        if (dealer.isPresent()) {
            logger.info("Dealer found with email: {}", username);
            return new CustomUserDetails(dealer.get());
        }

        // Check for User
        Optional<com.GpsTracker.Thinture.model.User> user = Optional.ofNullable(userRepository.findByEmail(username));
        if (user.isPresent()) {
            logger.info("User found with email: {}", username);
            return new CustomUserDetails(user.get());
        }

        // Check for Client (Newly added)
        Optional<Client> client = Optional.ofNullable(clientRepository.findByEmail(username));
        if (client.isPresent()) {
            logger.info("Client found with email: {}", username);
            return new CustomUserDetails(client.get());  // Ensures correct role handling
        }

        // If no user is found
        logger.error("User not found with email: {}", username);
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}
*/
    
    
