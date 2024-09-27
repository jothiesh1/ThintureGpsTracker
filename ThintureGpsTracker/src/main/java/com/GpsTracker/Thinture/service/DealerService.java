package com.GpsTracker.Thinture.service;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;


        
        
        
        
        
@Service
public class DealerService {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private SuperAdminRepository superAdminRepository;


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
}
