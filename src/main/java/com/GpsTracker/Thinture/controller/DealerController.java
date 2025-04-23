package com.GpsTracker.Thinture.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Handle form submission for adding a dealer.
     * 
     * 
     * 19/09/2024 code for superadmin id added into the table 
     */
    @PostMapping("/add")
    public ResponseEntity<String> addDealer(@RequestBody Dealer dealer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();

        try {
            dealerService.addDealer(dealer, loggedInUser);
            return ResponseEntity.ok("Dealer added successfully!");
        } catch (Exception e) {
            logger.error("❌ Error adding dealer: {}", e.getMessage(), e);
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
    
    
    //toggle button lock dealer_view.html
    @PutMapping("/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<String> toggleDealerStatus(@PathVariable Long id) {
        try {
            Dealer dealer = dealerService.toggleStatus(id);
            String message = dealer.isStatus() ? "Unlocked (Active)" : "Locked (Inactive)";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dealer not found with ID: " + id);
        }
    }

    
    
    
    //07/01/2025
    
    
    
    /**
     * Fetch all dealers with their names and IDs.
     */
    @GetMapping("/alll")
    public ResponseEntity<List<Map<String, Object>>> getAllDealers() {
        try {
            List<Dealer> dealers = dealerService.getAllDealers();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Dealer dealer : dealers) {
                Map<String, Object> dealerInfo = new HashMap<>();
                dealerInfo.put("id", dealer.getId());
                dealerInfo.put("name", dealer.getCompanyName());
                response.add(dealerInfo);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
