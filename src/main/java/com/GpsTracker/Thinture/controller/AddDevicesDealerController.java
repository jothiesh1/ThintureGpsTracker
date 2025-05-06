package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;

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

    // ✅ Add single device using session-based role
    @PostMapping("/add-single")
    public ResponseEntity<Map<String, String>> addSingleVehicle(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();
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

            // Optionally set default values (lat/lng = 0, status = 'N1', etc.)
            lastKnown.setLatitude(0.0);
            lastKnown.setLongitude(0.0);
            lastKnown.setStatus("N1");  // default
            lastKnown.setTimestamp(new Timestamp(System.currentTimeMillis()));
            lastKnown.setVehicleStatus("INACTIVE");
            lastKnown.setIgnition("OFF");
            lastKnown.setSpeed("0");

            // ✅ Set role IDs same as Vehicle
            lastKnown.setSuperadmin_id(vehicle.getSuperadmin_id());
            lastKnown.setAdmin_id(vehicle.getAdmin_id());
            lastKnown.setDealer_id(vehicle.getDealer_id());
            lastKnown.setClient_id(vehicle.getClient_id());
            lastKnown.setUser_id(vehicle.getUser_id());

            lastKnownRepository.save(lastKnown);
            logger.info("✅ LastKnown initialized: SerialNo={}, IMEI={}, DealerId={}", serialNo, imei, selectedDealerId);

            response.put("success", "true");
            response.put("message", "Vehicle and LastKnown record saved successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error saving vehicle/lastKnown", e);
            response.put("success", "false");
            response.put("message", "Failed to save: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // ✅ Add multiple devices using session-based role
 // ✅ Add multiple devices using session-based role
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
            for (Map<String, Object> deviceMap : devices) {
                String serialNo = (String) deviceMap.get("serialNo");
                String imei = (String) deviceMap.get("imei");

                if (serialNo == null || imei == null || serialNo.isBlank() || imei.isBlank()) {
                    logger.warn("⚠️ Skipping incomplete device: {}", deviceMap);
                    continue;
                }

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

                logger.info("✅ Saved device + last known: SerialNo={}, IMEI={}, DealerId={}", serialNo, imei, dealerId);
                savedCount++;
            }

            response.put("success", "true");
            response.put("message", savedCount + " devices added successfully .");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error adding devices", e);
            response.put("success", "false");
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
