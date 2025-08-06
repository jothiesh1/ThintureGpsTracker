package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Timestamp;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.security.AuthenticationFacade;
import com.GpsTracker.Thinture.service.DealerService;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import com.GpsTracker.Thinture.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/dealer")
public class AddDevicesDealerController {

    private static final Logger logger = LoggerFactory.getLogger(AddDevicesDealerController.class);

    @Autowired private DealerService dealerService;
    @Autowired private DealerRepository dealerRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleService vehicleService;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private AuthenticationFacade authenticationFacade;
    @Autowired private UserTypeFilterService userTypeFilterService;
    
    @Autowired private VehicleLastLocationRepository lastKnownRepository;

    // ✅ Add single device with duplicate checking
    @PostMapping("/add-single")
    public ResponseEntity<Map<String, Object>> addSingleVehicle(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("📌 Received request to add single vehicle: {}", payload);

            String serialNo = (String) payload.get("serialNo");
            String imei = (String) payload.get("imei");
            Long selectedDealerId = payload.get("dealerId") != null 
                    ? Long.parseLong(payload.get("dealerId").toString()) 
                    : null;

            if (serialNo == null || imei == null || selectedDealerId == null) {
                throw new IllegalArgumentException("serialNo, imei, and dealerId are required.");
            }

            // ✅ Check for duplicates before inserting
            boolean serialExists = vehicleRepository.existsBySerialNo(serialNo.trim());
            boolean imeiExists = vehicleRepository.existsByImei(imei.trim());

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
            vehicle.setSerialNo(serialNo.trim());
            vehicle.setImei(imei.trim());
            vehicle.setDealer_id(selectedDealerId);
            assignRoleIds(vehicle, userInfo);
            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle saved: SerialNo={}, IMEI={}, DealerId={}", serialNo, imei, selectedDealerId);

            // ✅ Save to VehicleLastLocation table
            VehicleLastLocation lastKnown = new VehicleLastLocation();
            lastKnown.setSerialNo(serialNo.trim());
            lastKnown.setImei(imei.trim());
            lastKnown.setDealer_id(selectedDealerId);

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
            logger.info("✅ LastKnown initialized: SerialNo={}, IMEI={}, DealerId={}", serialNo, imei, selectedDealerId);

            response.put("success", true);
            response.put("message", "Vehicle and LastKnown record saved successfully.");
            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException e) {
            logger.error("❌ Duplicate entry detected", e);
            response.put("success", false);
            response.put("message", "Duplicate entry detected. This Serial Number or IMEI already exists.");
            response.put("error_type", "DUPLICATE_ENTRY");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            logger.error("❌ Error saving vehicle/lastKnown", e);
            response.put("success", false);
            response.put("message", "Failed to save: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ✅ Add multiple devices with detailed duplicate reporting
    @PostMapping("/add-multiple")
    public ResponseEntity<Map<String, Object>> addMultipleDevices(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> devices = (List<Map<String, Object>>) payload.get("devices");
            Long dealerId = payload.get("dealerId") != null
                    ? Long.parseLong(payload.get("dealerId").toString())
                    : null;

            if (devices == null || devices.isEmpty() || dealerId == null) {
                throw new IllegalArgumentException("❌ Missing devices list or dealerId");
            }

            String email = authenticationFacade.getAuthenticatedEmail();
            UserTypeFilterService.UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
            if (userInfo == null) throw new RuntimeException("Logged-in user not recognized.");

            int savedCount = 0;
            int duplicateCount = 0;
            List<Map<String, String>> duplicateDevices = new ArrayList<>();
            List<Map<String, String>> savedDevices = new ArrayList<>();

            for (Map<String, Object> deviceMap : devices) {
                String serialNo = (String) deviceMap.get("serialNo");
                String imei = (String) deviceMap.get("imei");

                if (serialNo == null || imei == null || serialNo.isBlank() || imei.isBlank()) {
                    logger.warn("⚠️ Skipping incomplete device: {}", deviceMap);
                    continue;
                }

                // ✅ Check for duplicates
                boolean serialExists = vehicleRepository.existsBySerialNo(serialNo.trim());
                boolean imeiExists = vehicleRepository.existsByImei(imei.trim());

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
                    vehicle.setSerialNo(serialNo.trim());
                    vehicle.setImei(imei.trim());
                    vehicle.setDealer_id(dealerId);
                    assignRoleIds(vehicle, userInfo);
                    vehicleRepository.save(vehicle);

                    // ✅ Save to VehicleLastLocation
                    VehicleLastLocation lastKnown = new VehicleLastLocation();
                    lastKnown.setSerialNo(serialNo.trim());
                    lastKnown.setImei(imei.trim());
                    lastKnown.setDealer_id(dealerId);

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

                    logger.info("✅ Saved device + last known: SerialNo={}, IMEI={}, DealerId={}", serialNo, imei, dealerId);
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
            logger.error("❌ Error adding devices", e);
            response.put("success", false);
            response.put("message", "Failed to add devices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ✅ Helper: Assign role-based creator ID to vehicle
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

    // ✅ Fetch all dealers
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllDealers() {
        try {
            logger.info("Fetching all dealers...");
            List<Dealer> dealers = dealerService.getAllDealers();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Dealer dealer : dealers) {
                Map<String, Object> dealerInfo = new HashMap<>();
                dealerInfo.put("id", dealer.getId());
                dealerInfo.put("name", dealer.getCompanyName());
                response.add(dealerInfo);
            }

            logger.info("Successfully fetched {} dealers", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching dealers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}