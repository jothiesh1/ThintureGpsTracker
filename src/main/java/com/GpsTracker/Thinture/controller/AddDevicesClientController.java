package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.ClientService;
import com.GpsTracker.Thinture.service.VehicleService;

import java.util.*;


/** ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * 
 *      𝙅
 *      𝙊
 *      𝙏
 *      𝙃
 *      𝙄
 *      𝙀
 *      𝙎
 *      𝙃
 * 
 * Senior Developer, R&D 
 * 
 * ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 */
@RestController
@RequestMapping("/client")
public class AddDevicesClientController {

    private static final Logger logger = LoggerFactory.getLogger(AddDevicesClientController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private VehicleService vehicleService;

    /**
     * Endpoint to add a single serial number and IMEI with deviceId for a client.
     */
    /**
     * Endpoint to add a single serial number and IMEI for a client.
     */
    @PostMapping("/add-single")
    public ResponseEntity<Map<String, String>> addSingleVehicle(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("📥 Received request to add single vehicle for client: {}", payload);

            if (!payload.containsKey("serialNo") || !payload.containsKey("imei") || !payload.containsKey("clientId")) {
                throw new IllegalArgumentException("❌ Missing required parameters: serialNo, imei, clientId");
            }

            String serialNo = payload.get("serialNo").toString();
            String imei = payload.get("imei").toString();
            Long clientId = Long.parseLong(payload.get("clientId").toString());

            // ✅ Call VehicleService Instead of ClientService
            vehicleService.addClientVehicle(serialNo, imei, clientId);

            response.put("success", "true");
            response.put("message", "Single vehicle added successfully for client.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "Failed to add single vehicle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    /**
     * Endpoint to add a range of serial numbers (dual functionality) for a client.
     */
    @PostMapping("/add-dual")
    public ResponseEntity<Map<String, String>> addDualVehicles(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("📥 Received request to add dual vehicles for client: {}", payload);

            if (!payload.containsKey("serialNumbers") || !payload.containsKey("clientId")) {
                throw new IllegalArgumentException("❌ Missing required parameters: serialNumbers, clientId");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> serialNumbers = (List<Map<String, Object>>) payload.get("serialNumbers");
            Long clientId = Long.parseLong(payload.get("clientId").toString());

            // ✅ Use your existing service method that handles Map<String, Object>
            vehicleService.addDualClientVehicles(serialNumbers, clientId);

            response.put("success", "true");
            response.put("message", "Dual vehicles added successfully for client.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Exception in /add-dual: {}", e.getMessage(), e);  // 🚨 Add this to log exceptions
            response.put("success", "false");
            response.put("message", "Failed to add dual vehicles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    /**
     * Fetch all clients with their names and IDs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllClients() {
        try {
            logger.info("Fetching all clients...");
            List<Client> clients = clientService.getAllClients();

            if (clients.isEmpty()) {
                logger.warn("No clients found in the database.");
            } else {
                logger.info("Total Clients Fetched: {}", clients.size());
            }

            List<Map<String, Object>> response = new ArrayList<>();
            for (Client client : clients) {
                logger.debug("Processing Client - ID: {}, Name: {}", client.getId(), client.getCompanyName());

                Map<String, Object> clientInfo = new HashMap<>();
                clientInfo.put("id", client.getId());
                clientInfo.put("name", client.getCompanyName());
                response.add(clientInfo);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching clients: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
