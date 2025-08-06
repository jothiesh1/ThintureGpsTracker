package com.GpsTracker.Thinture.service;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;


        
        
        
        
        
@Service
public class DealerService {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private SuperAdminRepository superAdminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DealerService.class);

    /**
     * Save a new dealer and associate it with an Admin and a SuperAdmin.
     * 
     * @param dealer  the dealer to save
     * @param adminId the ID of the admin who creates the dealer
     * @return the saved dealer
     */
    public void saveDealer(Dealer dealer) {
        dealerRepository.save(dealer);
    }


    
    /**
     * ✅ Add a new dealer
     */
    public Dealer addDealer(Dealer dealer, CustomUserDetails loggedInUser) {
        Long userId = loggedInUser.getId();
        String role = loggedInUser.getAuthorities().iterator().next().getAuthority();

        logger.info("🛠 Adding dealer initiated by: {} | ID: {} | Role: {}", loggedInUser.getUsername(), userId, role);

        if (role.equals("ROLE_ADMIN")) {
            Admin admin = adminRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("❌ Admin not found for ID: {}", userId);
                    return new RuntimeException("Admin not found (ID: " + userId + ")");
                });

            dealer.setAdmin(admin);
            dealer.setAdmin_id(admin.getId());
            dealer.setSuperAdmin(null);
            dealer.setSuperadmin_id(null);

            logger.info("✅ Dealer linked to Admin ID: {}", admin.getId());

        } else if (role.equals("ROLE_SUPERADMIN")) {
            SuperAdmin superAdmin = superAdminRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("❌ SuperAdmin not found for ID: {}", userId);
                    return new RuntimeException("SuperAdmin not found (ID: " + userId + ")");
                });

            dealer.setSuperAdmin(superAdmin);
            dealer.setSuperadmin_id(superAdmin.getId());
            dealer.setAdmin(null);
            dealer.setAdmin_id(null);

            logger.info("✅ Dealer linked to SuperAdmin ID: {}", superAdmin.getId());

        } else {
            logger.warn("⚠️ Unauthorized role '{}' attempted to add dealer", role);
            throw new RuntimeException("Unauthorized role to create a dealer");
        }

        dealer.setPassword(passwordEncoder.encode(dealer.getPassword()));
        Dealer savedDealer = dealerRepository.save(dealer);

        logger.info("🆗 Dealer saved with ID: {} | Name: {}", savedDealer.getId(), savedDealer.getCompanyName());
        return savedDealer;
    }
    
    
    
    
    /**
     * Find a dealer by its ID.
     * 
     * @param id the dealer ID
     * @return the dealer if found, otherwise null
     */
    public Dealer findDealerById(Long id) {
        logger.info("Finding dealer by ID: {}", id);
        return dealerRepository.findById(id).orElse(null);
    }

    /**
     * Fetch all dealers from the database.
     * 
     * @return a list of all dealers
     */
    public List<Dealer> findAllDealers() {
        logger.info("Fetching all dealers from the database");
        return dealerRepository.findAll();
    }

    /**
     * Get all dealers and log the operation.
     * 
     * @return a list of all dealers
     */
    public List<Dealer> getAllDealers() {
        logger.info("Fetching all dealers from the database.");
        try {
            List<Dealer> dealers = dealerRepository.findAll(); // Fetch all dealers from the database
            logger.info("Successfully fetched {} dealers.", dealers.size());
            return dealers;
        } catch (Exception e) {
            logger.error("Error occurred while fetching dealers from the database: ", e);
            throw e;
        }
    }

    /**
     * Update an existing dealer.
     * 
     * @param id            the ID of the dealer to update
     * @param updatedDealer the updated dealer information
     */
    public void updateDealer(Long id, Dealer updatedDealer) {
        logger.info("Updating dealer with ID: {}", id);
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer not found with ID: " + id));

        dealer.setCompanyName(updatedDealer.getCompanyName());
        dealer.setAddress(updatedDealer.getAddress());
        dealer.setEmail(updatedDealer.getEmail());
        dealer.setPhone(updatedDealer.getPhone());
        dealer.setCountry(updatedDealer.getCountry());
        dealer.setAdmin(updatedDealer.getAdmin());
        dealer.setSuperAdmin(updatedDealer.getSuperAdmin());

        dealerRepository.save(dealer);
        logger.info("Dealer updated with ID: {}", id);
    }

    /**
     * Delete a dealer by its ID.
     * 
     * @param id the ID of the dealer to delete
     */
    public void deleteDealer(Long id) {
        logger.info("Deleting dealer with ID: {}", id);
        dealerRepository.deleteById(id);
        logger.info("Dealer deleted with ID: {}", id);
    }
    /**
     * Adds a single serial number to a dealer's list of serials.
     * 
     * @param dealerName   The name of the dealer.
     * @param serialNumber The serial number to add.
     */
    public void addSingleSerial(String dealerName, String serialNumber) {
        // Find the dealer by company name
        Dealer dealer = dealerRepository.findByCompanyName(dealerName);
        if (dealer == null) {
            throw new IllegalArgumentException("Dealer not found with company name: " + dealerName);
        }

        // Add the serial number to the dealer's list of serial numbers
        dealer.addSerial(serialNumber);
        dealerRepository.save(dealer);

        System.out.println("Added single serial " + serialNumber + " to dealer " + dealerName);
    }

    /**
     * Adds a range of serial numbers to a dealer's list of serials, excluding a specific serial number.
     * 
     * @param dealerName   The name of the dealer.
     * @param startSerial  The starting serial number of the range.
     * @param endSerial    The ending serial number of the range.
     * @param removedSerial The serial number to exclude.
     */
    public void addDualSerials(String dealerName, int startSerial, int endSerial, int removedSerial) {
        // Find the dealer by company name
        Dealer dealer = dealerRepository.findByCompanyName(dealerName);
        if (dealer == null) {
            throw new IllegalArgumentException("Dealer not found with company name: " + dealerName);
        }

        // Add serial numbers to the dealer, skipping the removed serial
        for (int i = startSerial; i <= endSerial; i++) {
            if (i != removedSerial) {
                dealer.addSerial(String.valueOf(i));
            }
        }

        dealerRepository.save(dealer);
        System.out.println("Added serials from " + startSerial + " to " + endSerial + " (excluding " + removedSerial + ") to dealer " + dealerName);
    }


    public Dealer toggleStatus(Long id) {
        Dealer dealer = dealerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dealer not found"));
        dealer.setStatus(!dealer.isStatus());
        return dealerRepository.save(dealer);
    }


    
    
}


