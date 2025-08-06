package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.service.ClientService;
import com.GpsTracker.Thinture.service.VehicleService;
import com.GpsTracker.Thinture.security.AuthenticationFacade;
import com.GpsTracker.Thinture.service.UserTypeFilterService;

import java.sql.Timestamp;
import java.util.*;

/** ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * 
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
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private VehicleLastLocationRepository lastKnownRepository;
    
    @Autowired
    private AuthenticationFacade authenticationFacade;
    
    @Autowired
    private UserTypeFilterService userTypeFilterService;

    /**
     * ✅ Endpoint to add a single serial number and IMEI for a client with duplicate checking.
     */
    @PostMapping("/add-single")
    public ResponseEntity<Map<String, Object>> addSingleVehicle(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("📥 Received request to add single vehicle for client: {}", payload);

            if (!payload.containsKey("serialNo") || !payload.containsKey("imei") || !payload.containsKey("clientId")) {
                throw new IllegalArgumentException("❌ Missing required parameters: serialNo, imei, clientId");
            }

            String serialNo = payload.get("serialNo").toString().trim();
            String imei = payload.get("imei").toString().trim();
            Long clientId = Long.parseLong(payload.get("clientId").toString());

            // ✅ Check for duplicates before inserting
            boolean serialExists = vehicleRepository.existsBySerialNo(serialNo);
            boolean imeiExists = vehicleRepository.existsByImei(imei);

            if (serialExists || imeiExists) {
                List<String> duplicates = new ArrayList<>();
                if (serialExists) duplicates.add("Serial Number: " + serialNo);
                if (imeiExists) duplicates.add("IMEI: " + imei);
                
                response.put("success", false);
                response.put("message", "Duplicate entries found: " + String.join(", ", duplicates));
                response.put("duplicates", duplicates);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Get logged-in user role and ID
            String email = authenticationFacade.getAuthenticatedEmail();
            UserTypeFilterService.UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
            if (userInfo == null) throw new RuntimeException("Logged-in user not recognized.");

            // ✅ Save to Vehicle table
            Vehicle vehicle = new Vehicle();
            vehicle.setSerialNo(serialNo);
            vehicle.setImei(imei);
            vehicle.setClient_id(clientId);
            assignRoleIds(vehicle, userInfo);
            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle saved: SerialNo={}, IMEI={}, ClientId={}", serialNo, imei, clientId);

            // ✅ Save to VehicleLastLocation table
            VehicleLastLocation lastKnown = new VehicleLastLocation();
            lastKnown.setSerialNo(serialNo);
            lastKnown.setImei(imei);
            lastKnown.setClient_id(clientId);

            // Set default values
            lastKnown.setLatitude(0.0);
            lastKnown.setLongitude(0.0);
            lastKnown.setStatus("N1");
            lastKnown.setTimestamp(new Timestamp(System.currentTimeMillis()));
            lastKnown.setVehicleStatus("INACTIVE");
            lastKnown.setIgnition("OFF");
            lastKnown.setSpeed("0");

            // Set role IDs same as Vehicle
            lastKnown.setSuperadmin_id(vehicle.getSuperadmin_id());
            lastKnown.setAdmin_id(vehicle.getAdmin_id());
            lastKnown.setDealer_id(vehicle.getDealer_id());
            lastKnown.setClient_id(vehicle.getClient_id());
            lastKnown.setUser_id(vehicle.getUser_id());

            lastKnownRepository.save(lastKnown);
            logger.info("✅ LastKnown initialized: SerialNo={}, IMEI={}, ClientId={}", serialNo, imei, clientId);

            response.put("success", true);
            response.put("message", "Single vehicle added successfully for client.");
            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException e) {
            logger.error("❌ Duplicate entry detected", e);
            response.put("success", false);
            response.put("message", "Duplicate entry detected. This Serial Number or IMEI already exists.");
            response.put("error_type", "DUPLICATE_ENTRY");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            logger.error("❌ Exception in /add-single: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to add single vehicle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * ✅ Endpoint to add multiple vehicles for a client with detailed duplicate reporting.
     */
    @PostMapping("/add-dual")
    public ResponseEntity<Map<String, Object>> addDualVehicles(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("📥 Received request to add dual vehicles for client: {}", payload);

            if (!payload.containsKey("serialNumbers") || !payload.containsKey("clientId")) {
                throw new IllegalArgumentException("❌ Missing required parameters: serialNumbers, clientId");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> serialNumbers = (List<Map<String, Object>>) payload.get("serialNumbers");
            Long clientId = Long.parseLong(payload.get("clientId").toString());

            if (serialNumbers == null || serialNumbers.isEmpty()) {
                throw new IllegalArgumentException("❌ serialNumbers list is empty");
            }

            String email = authenticationFacade.getAuthenticatedEmail();
            UserTypeFilterService.UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
            if (userInfo == null) throw new RuntimeException("Logged-in user not recognized.");

            int savedCount = 0;
            int duplicateCount = 0;
            List<Map<String, String>> duplicateDevices = new ArrayList<>();
            List<Map<String, String>> savedDevices = new ArrayList<>();

            for (Map<String, Object> deviceMap : serialNumbers) {
                String serialNo = deviceMap.get("serialNo") != null ? deviceMap.get("serialNo").toString().trim() : null;
                String imei = deviceMap.get("imei") != null ? deviceMap.get("imei").toString().trim() : null;

                if (serialNo == null || imei == null || serialNo.isBlank() || imei.isBlank()) {
                    logger.warn("⚠️ Skipping incomplete device: {}", deviceMap);
                    continue;
                }

                // ✅ Check for duplicates
                boolean serialExists = vehicleRepository.existsBySerialNo(serialNo);
                boolean imeiExists = vehicleRepository.existsByImei(imei);

                if (serialExists || imeiExists) {
                    Map<String, String> duplicateInfo = new HashMap<>();
                    duplicateInfo.put("serialNo", serialNo);
                    duplicateInfo.put("imei", imei);
                    
                    List<String> duplicateReasons = new ArrayList<>();
                    if (serialExists) duplicateReasons.add("Serial Number exists");
                    if (imeiExists) duplicateReasons.add("IMEI exists");
                    
                    duplicateInfo.put("reason", String.join(", ", duplicateReasons));
                    duplicateDevices.add(duplicateInfo);
                    duplicateCount++;
                    
                    logger.warn("⚠️ Skipping duplicate device: SerialNo={}, IMEI={}, Reason={}", 
                               serialNo, imei, String.join(", ", duplicateReasons));
                    continue;
                }

                try {
                    // ✅ Save to Vehicle
                    Vehicle vehicle = new Vehicle();
                    vehicle.setSerialNo(serialNo);
                    vehicle.setImei(imei);
                    vehicle.setClient_id(clientId);
                    assignRoleIds(vehicle, userInfo);
                    vehicleRepository.save(vehicle);

                    // ✅ Save to VehicleLastLocation
                    VehicleLastLocation lastKnown = new VehicleLastLocation();
                    lastKnown.setSerialNo(serialNo);
                    lastKnown.setImei(imei);
                    lastKnown.setClient_id(clientId);

                    lastKnown.setLatitude(0.0);
                    lastKnown.setLongitude(0.0);
                    lastKnown.setStatus("N1");
                    lastKnown.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    lastKnown.setVehicleStatus("INACTIVE");
                    lastKnown.setIgnition("OFF");
                    lastKnown.setSpeed("0");

                    lastKnown.setSuperadmin_id(vehicle.getSuperadmin_id());
                    lastKnown.setAdmin_id(vehicle.getAdmin_id());
                    lastKnown.setDealer_id(vehicle.getDealer_id());
                    lastKnown.setClient_id(vehicle.getClient_id());
                    lastKnown.setUser_id(vehicle.getUser_id());

                    lastKnownRepository.save(lastKnown);

                    Map<String, String> savedInfo = new HashMap<>();
                    savedInfo.put("serialNo", serialNo);
                    savedInfo.put("imei", imei);
                    savedDevices.add(savedInfo);

                    logger.info("✅ Saved device + last known: SerialNo={}, IMEI={}, ClientId={}", serialNo, imei, clientId);
                    savedCount++;
                    
                } catch (DataIntegrityViolationException e) {
                    // Handle race condition where duplicate might be inserted between check and save
                    Map<String, String> duplicateInfo = new HashMap<>();
                    duplicateInfo.put("serialNo", serialNo);
                    duplicateInfo.put("imei", imei);
                    duplicateInfo.put("reason", "Duplicate detected during save (race condition)");
                    duplicateDevices.add(duplicateInfo);
                    duplicateCount++;
                    logger.warn("⚠️ Race condition duplicate: SerialNo={}, IMEI={}", serialNo, imei);
                }
            }

            // ✅ Build comprehensive response
            response.put("success", true);
            response.put("savedCount", savedCount);
            response.put("duplicateCount", duplicateCount);
            response.put("totalProcessed", savedCount + duplicateCount);
            
            if (savedCount > 0) {
                response.put("savedDevices", savedDevices);
            }
            
            if (duplicateCount > 0) {
                response.put("duplicateDevices", duplicateDevices);
                response.put("message", String.format("Processing completed: %d devices saved, %d duplicates skipped.", 
                                                    savedCount, duplicateCount));
            } else {
                response.put("message", String.format("All %d devices added successfully.", savedCount));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Exception in /add-dual: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to add dual vehicles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * ✅ Helper: Assign role-based creator ID to vehicle
     */
    private void assignRoleIds(Vehicle vehicle, UserTypeFilterService.UserTypeResult userInfo) {
        switch (userInfo.getRole()) {
            case "SUPERADMIN" -> vehicle.setSuperadmin_id(userInfo.id());
            case "ADMIN" -> vehicle.setAdmin_id(userInfo.id());
            case "DEALER" -> vehicle.setDealer_id(userInfo.id());
            case "CLIENT" -> vehicle.setClient_id(userInfo.id());
            case "USER" -> vehicle.setUser_id(userInfo.id());
            default -> logger.warn("⚠️ Unknown role: {}", userInfo.getRole());
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