package com.GpsTracker.Thinture.controller;


import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.service.DealerService;
import com.GpsTracker.Thinture.service.VehicleService;











import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.ClientService;
import com.GpsTracker.Thinture.service.VehicleService;

import java.util.*;
@RestController
@RequestMapping("/vehicless") // ✅ Matches frontend API calls
public class DealerClientController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(DealerClientController.class);
    
    
    @PostMapping("/add")
    public ResponseEntity<String> addClient(@RequestBody Client client, Authentication authentication) {
        // Get the logged-in user
        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();

        // Call the service method with the logged-in user
        clientService.addClient(client, loggedInUser);

        return ResponseEntity.ok("Client added successfully");
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
}
