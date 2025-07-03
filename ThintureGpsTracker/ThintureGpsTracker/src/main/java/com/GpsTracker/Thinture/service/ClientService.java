package com.GpsTracker.Thinture.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClientService {

	
	

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;
    @Autowired
    private ClientRepository clientRepository;


    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    /**
     * Save a new client to the database.
     *
     * @param client the client object to be saved.
     * @return the saved client object.
     * @throws IllegalArgumentException if the client object is null or already exists.
    
    public Client saveClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client object cannot be null.");
        }

        // Check for existing client by email
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Client with email " + client.getEmail() + " already exists.");
        }

        return clientRepository.save(client);
    }

    /**
     * Find a client by their email.
     *
     * @param email the email of the client.
     * @return an Optional containing the client if found, or empty if not found.
     */
    public Optional<Client> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }

        return Optional.of(clientRepository.findByEmail(email));
    }

    /**
     * Retrieve all clients from the database.
     *
     * @return a list of all clients.
     */
  
    
  

    // ✅ Fetch All Clients
    public List<Client> getAllClients() {
        logger.info("📡 Fetching all clients...");
        return clientRepository.findAll();
    }

  
    // ✅ Fetch Client by Dealer ID
    public Optional<Client> getClientByDealerId(Long dealerId) {
        logger.info("📡 Fetching client linked to Dealer ID: {}", dealerId);
        return clientRepository.getClientByDealerId(dealerId);
    }

    

    
    
    public List<Client> getClientsStartingWith(String query) {
        logger.info("🔍 Searching clients with name or ID starting with '{}'", query);
        
        // If query is numeric, search by ID as well
        if (query.matches("\\d+")) { 
            return clientRepository.findClientsByIdOrName(query);
        }
        
        return clientRepository.findClientsByQuery(query);
    }
    
    // ✅ Add a new client
    public Client addClient(Client client, CustomUserDetails loggedInUser) {
        Long userId = loggedInUser.getId();
        String role = loggedInUser.getAuthorities().iterator().next().getAuthority();

        logger.info("Adding client. Logged-in user: {} (ID: {}), Role: {}", loggedInUser.getUsername(), userId, role);

        // Assign the client to the logged-in user's category
        if (role.equals("ROLE_ADMIN")) {
            Admin admin = adminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
            client.setAdmin(admin);
            client.setDealer(null); // Ensure only one of them is set
            client.setSuperAdmin(null);
            logger.info("Client assigned to Admin ID: {}", userId);
        } else if (role.equals("ROLE_DEALER")) {
            Dealer dealer = dealerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Dealer not found"));
            client.setDealer(dealer);
            client.setAdmin(null);
            client.setSuperAdmin(null);
            logger.info("Client assigned to Dealer ID: {}", userId);
        } else if (role.equals("ROLE_SUPERADMIN")) {
            SuperAdmin superAdmin = superAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));
            client.setSuperAdmin(superAdmin);
            client.setDealer(null);
            client.setAdmin(null);
            logger.info("Client assigned to SuperAdmin ID: {}", userId);
        } else {
            throw new RuntimeException("Unauthorized role to create a client");
        }

        return clientRepository.save(client);
    }

}