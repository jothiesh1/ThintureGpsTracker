package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.service.AdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
@RequestMapping("/admins")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;
    

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;
    
    
    @GetMapping("/list")
    public String listAdmins(Model model) {
        logger.info("Received request to list all admins.");
        model.addAttribute("admins", adminService.getAllAdmins());
        return "/dashboard"; // Name of the HTML page for listing admins
    }

    @GetMapping("/delete/{id}")
    public String deleteAdmin(@PathVariable Long id, Model model) {
        logger.info("Received request to delete admin with ID: {}", id);
        try {
            adminService.deleteAdmin(id);
            logger.info("Successfully deleted admin with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting admin with ID: {}", id, e);
        }
        return "redirect:/admins/list";
    }


    
    
    
    /**
     * Add an Admin by an authenticated Admin or SuperAdmin.
     * @param admin The Admin entity to be added.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/addAdmin")
    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
        logger.info("Received request to add admin: {}", admin);

        // Fetch authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            logger.info("Currently logged-in user: {}", currentUsername);

            // Fetch the SuperAdmin details using the logged-in user's email
            SuperAdmin superAdmin = superAdminRepository.findByEmail(currentUsername);

            if (superAdmin != null) {
                // Set SuperAdmin reference in Admin entity
                admin.setSuperAdmin(superAdmin);
                logger.info("SuperAdmin ID {} set for the admin.", superAdmin.getId());

                // Save admin
                try {
                    adminService.saveAdmin(admin);
                    logger.info("Admin added successfully!");
                    return ResponseEntity.ok("Admin added successfully!");
                } catch (Exception e) {
                    logger.error("Error occurred while adding admin", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding admin");
                }
            } else {
                logger.error("SuperAdmin not found for username: {}", currentUsername);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SuperAdmin not found");
            }
        } else {
            logger.warn("User is not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    }

    }
//    @PostMapping("/addAdmin")
//    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
//        logger.info("Received request to add admin: {}", admin);
//        try {
//            adminService.saveAdmin(admin);
//            logger.info("Admin added successfully!");
//            return ResponseEntity.ok("Admin added successfully!");
//        } catch (Exception e) {
//            logger.error("Error occurred while adding admin", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding admin");
//        }
//    }



