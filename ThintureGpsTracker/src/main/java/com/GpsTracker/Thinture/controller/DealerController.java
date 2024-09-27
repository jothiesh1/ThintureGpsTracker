package com.GpsTracker.Thinture.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.service.DealerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dealers")
public class DealerController {

    private static final Logger logger = LoggerFactory.getLogger(DealerController.class);

    @Autowired
    private DealerService dealerService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    /**
     * Display the Add Dealer form.
     */
    @GetMapping("/add")
    public String showAddDealerForm() {
        logger.info("Displaying the Add Dealer Form.");
        return "add_dealer";
    }

    /**
     * Handle form submission for adding a dealer.
     * 
     * 
     * 19/09/2024 code for superadmin id added into the table 
     */
    @PostMapping("/add")
    public String addDealer(@ModelAttribute Dealer dealer, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            logger.info("Current logged-in user: {}", currentUsername);

            // Check the role of the user and fetch the corresponding ID
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // Admin flow
                Admin admin = adminRepository.findByEmailIgnoreCase(currentUsername);
                if (admin != null) {
                    dealer.setAdmin(admin);  // Set Admin reference in Dealer entity
                    logger.info("Admin ID {} set for the dealer.", admin.getId());
                } else {
                    logger.error("Admin not found for username: {}", currentUsername);
                    model.addAttribute("error", "Admin not found.");
                    return "add_dealer";  // Return back to the form with an error message
                }
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))) {
                // SuperAdmin flow
                SuperAdmin superAdmin = superAdminRepository.findByEmail(currentUsername);
                if (superAdmin != null) {
                    dealer.setSuperAdmin(superAdmin);  // Set SuperAdmin reference in Dealer entity
                    logger.info("SuperAdmin ID {} set for the dealer.", superAdmin.getId());
                } else {
                    logger.error("SuperAdmin not found for username: {}", currentUsername);
                    model.addAttribute("error", "SuperAdmin not found.");
                    return "add_dealer";  // Return back to the form with an error message
                }
            } else {
                logger.warn("User is neither Admin nor SuperAdmin: {}", currentUsername);
                model.addAttribute("error", "User is not authorized to add a dealer.");
                return "error";  // Return to error page
            }

            // Save the dealer entity
            try {
                dealerService.saveDealer(dealer);
                model.addAttribute("message", "Dealer added successfully!");
            } catch (Exception e) {
                logger.error("Error adding dealer: ", e);
                model.addAttribute("error", "Error adding dealer: " + e.getMessage());
            }

            return "add_dealer";  // Return success message after form submission
        } else {
            logger.warn("No authenticated user found.");
            model.addAttribute("error", "You must be logged in to add a dealer.");
            return "login";  // Redirect to login page if not authenticated
        }
    }








    /**
     * Handle JSON request for adding a dealer.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<String> addDealerJson(@RequestBody Dealer dealer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            logger.warn("User is not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        try {
            String currentPrincipalName = authentication.getName();

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                Long adminId = adminRepository.findIdByEmail(currentPrincipalName);
                if (adminId != null) {
                    Admin admin = new Admin();
                    admin.setId(adminId);
                    dealer.setAdmin(admin);
                    dealerService.saveDealer(dealer);
                    logger.info("Dealer added successfully by Admin: {}", currentPrincipalName);
                } else {
                    logger.error("Admin not found for email: {}", currentPrincipalName);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin not found.");
                }
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))) {
                Long superAdminId = superAdminRepository.findIdByEmail(currentPrincipalName);
                if (superAdminId != null) {
                    SuperAdmin superAdmin = new SuperAdmin();
                    superAdmin.setId(superAdminId);
                    dealer.setSuperAdmin(superAdmin);
                    dealerService.saveDealer(dealer);
                    logger.info("Dealer added successfully by SuperAdmin: {}", currentPrincipalName);
                } else {
                    logger.error("SuperAdmin not found for email: {}", currentPrincipalName);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SuperAdmin not found.");
                }
            }

            return ResponseEntity.ok("Dealer added successfully!");

        } catch (Exception e) {
            logger.error("Error occurred while adding dealer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding dealer.");
        }
    }



    /**
     * Display dealer details view.
     */
    @GetMapping("/view")
    public String showDealerDetails(Model model) {
        logger.info("Received request to view dealer details.");

        try {
            List<Dealer> dealers = dealerService.getAllDealers();
            model.addAttribute("dealers", dealers);
            logger.info("Fetched {} dealers from the database.", dealers.size());
        } catch (Exception e) {
            logger.error("Error occurred while fetching dealer details: ", e);
        }

        return "view_dealer";
    }

    /**
     * Provide dealer details in JSON format.
     */
    @GetMapping("/api/dealers")
    @ResponseBody
    public List<Dealer> getDealersJson() {
        logger.info("Received request to get dealer details in JSON format.");
        try {
            List<Dealer> dealers = dealerService.getAllDealers();
            logger.info("Fetched {} dealers from the database.", dealers.size());
            return dealers;
        } catch (Exception e) {
            logger.error("Error occurred while fetching dealer details: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Handle dealer edit requests.
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editDealer(@PathVariable Long id, @RequestBody Dealer dealer) {
        logger.info("Received request to edit dealer with ID: {}", id);
        try {
            dealerService.updateDealer(id, dealer);
            logger.info("Successfully updated dealer with ID: {}", id);
            return ResponseEntity.ok("Dealer updated successfully!");
        } catch (Exception e) {
            logger.error("Error updating dealer with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating dealer.");
        }
    }

    /**
     * Handle dealer delete requests.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDealer(@PathVariable Long id) {
        logger.info("Received request to delete dealer with ID: {}", id);
        try {
            dealerService.deleteDealer(id);
            logger.info("Successfully deleted dealer with ID: {}", id);
            return ResponseEntity.ok("Dealer deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting dealer with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting dealer.");
        }
    }
}
