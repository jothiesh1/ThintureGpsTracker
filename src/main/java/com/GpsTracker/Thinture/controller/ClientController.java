package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.service.AdminService;
import com.GpsTracker.Thinture.service.ClientService;
import com.GpsTracker.Thinture.service.VehicleService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/clients")
public class ClientController {

	 @Autowired
	    private VehicleService vehicleService;

	    @Autowired
	    private ClientService clientService;
	    
	    @Autowired
	    private ClientRepository clientRepository;


	    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	    
	    
	    @PostMapping("/add")
	    public ResponseEntity<String> addClient(@RequestBody Client client, Authentication authentication) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            logger.warn("Unauthorized attempt to add client.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }

	        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();

	        try {
	            Client savedClient = clientService.addClient(client, loggedInUser);
	            logger.info("✅ Client saved successfully with ID: {}", savedClient.getId());
	            return ResponseEntity.ok("Client added successfully");
	        } catch (Exception e) {
	            logger.error("❌ Error saving client: {}", e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding client");
	        }
	    }


	    // ✅ Auto-Complete: Search Serial Numbers
	 // ✅ Search Serial Numbers for Auto-Complete
	    @GetMapping("/search-serials")
	    public ResponseEntity<List<Map<String, Object>>> searchSerialNumbers(@RequestParam String query) {
	        logger.info("🔄 Searching Serial Numbers starting with: {}", query);

	        List<Vehicle> vehicles = vehicleService.getSerialNosStartingWithh(query);
	        if (vehicles.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
	        }

	        List<Map<String, Object>> serialData = vehicles.stream()
	            .map(vehicle -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("serialNo", vehicle.getSerialNo());
	                map.put("dealerId", vehicle.getDealer() != null ? vehicle.getDealer().getId() : null); // ✅ Include Dealer ID
	                return map;
	            })
	            .collect(Collectors.toList());

	        logger.info("✅ Found {} matching serial numbers.", serialData.size());
	        return ResponseEntity.ok(serialData);
	    }



	    // ✅ Search Clients for Auto-Complete
	    @GetMapping("/search-clients")
	    public ResponseEntity<List<Map<String, Object>>> searchClients(@RequestParam String query) {
	        logger.info("🔄 Searching Clients by Name or ID with query: {}", query);

	        List<Client> clients = clientService.getClientsStartingWith(query);
	        if (clients.isEmpty()) {
	            logger.warn("⚠️ No clients found for query: {}", query);
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
	        }

	        List<Map<String, Object>> clientData = clients.stream()
	            .map(client -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("id", client.getId());  
	                map.put("name", client.getCompanyName());
	                return map;
	            })
	            .collect(Collectors.toList());

	        logger.info("✅ Found {} matching clients.", clientData.size());
	        return ResponseEntity.ok(clientData);
	    }




	    // ✅ Update Client ID for the selected Serial Number
	    @PutMapping("/update-client")
	    public ResponseEntity<String> updateVehicleClient(@RequestParam String serialNo, @RequestParam Long clientId) {
	        logger.info("🔄 Updating Client ID {} for Serial No: {}", clientId, serialNo);

	        boolean updated = vehicleService.updateClientForVehicle(serialNo, clientId);
	        if (updated) {
	            return ResponseEntity.ok("Client ID updated successfully.");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle or Client not found.");
	        }
	    }
	    
	    
	    @PutMapping("/{id}/toggle-status")
	    public ResponseEntity<String> toggleClientStatus(@PathVariable Long id) {
	        try {
	            String result = clientService.toggleClientStatus(id);
	            return ResponseEntity.ok(result);
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        }
	    }
  
	    @PutMapping("/edit/{id}")
	    public ResponseEntity<String> editClient(@PathVariable Long id, @RequestBody Client updatedClient) {
	        try {
	            clientService.updateClient(id, updatedClient);
	            logger.info("✅ Client updated: ID {}", id);
	            return ResponseEntity.ok("Client updated successfully!");
	        } catch (Exception e) {
	            logger.error("❌ Error updating client with ID: {}", id, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating client.");
	        }
	    }
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
	        try {
	            clientService.deleteClient(id);
	            logger.info("🗑️ Deleted client with ID: {}", id);
	            return ResponseEntity.ok("Client deleted successfully!");
	        } catch (Exception e) {
	            logger.error("❌ Error deleting client with ID: {}", id, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting client.");
	        }
	    }

	    
	}


    /**
     * Retrieve and display all clients.
     *
     * @param model the model to add attributes.
     * @return the name of the Thymeleaf template for displaying clients.
     
    @GetMapping("/all")
    public String getAllClients(Model model) {
        logger.info("Fetching all clients.");
        try {
            model.addAttribute("clients", clientService.getAllClients());
            return "client_list"; // Assuming client_list.html is in templates
        } catch (Exception e) {
            logger.error("Error fetching clients: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Unable to retrieve client list. Please try again later.");
            return "error"; // Assuming error.html is a generic error page
        }
    }
    */

