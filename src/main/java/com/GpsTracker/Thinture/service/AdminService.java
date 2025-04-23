package com.GpsTracker.Thinture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.repository.AdminRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminRepository adminRepository;

    public void saveAdmin(Admin admin) {
        logger.info("Saving admin: {}", admin);
        try {
            adminRepository.save(admin);
            logger.info("Admin saved successfully");
        } catch (Exception e) {
            logger.error("Error occurred while saving admin", e);
            throw e; // Rethrow the exception after logging it
        }
    }

    public List<Admin> getAllAdmins() {
        logger.info("Fetching all admins.");
        List<Admin> admins = adminRepository.findAll();
        logger.info("Fetched {} admins from the database.", admins.size());
        return admins;
    }

    public Admin getAdminByEmail(String email) {
        logger.info("Fetching admin with email: {}", email);
        Admin admin = adminRepository.findByEmailIgnoreCase(email);
        if (admin != null) {
            logger.info("Admin found: {}", admin);
        } else {
            logger.warn("No admin found with email: {}", email);
        }
        return admin;
    }

    public void deleteAdmin(Long id) {
        logger.info("Deleting admin with ID: {}", id);
        try {
            adminRepository.deleteById(id);
            logger.info("Admin deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting admin with ID: {}", id, e);
            throw e; // Rethrow the exception after logging it
        }
    }
    
    
    
    
    //toogle lock button admin_user view html
    
    public Optional<Admin> toggleStatus(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setStatus(!admin.isStatus());
            adminRepository.save(admin);
        }
        return optionalAdmin;
    }
}
