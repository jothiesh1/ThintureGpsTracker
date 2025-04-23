package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.service.DealerService;
import com.GpsTracker.Thinture.service.VehicleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
@RequestMapping("/dealer")
public class AddDevicesDealerController {

    private static final Logger logger = LoggerFactory.getLogger(AddDevicesDealerController.class);

    @Autowired
    private DealerService dealerService;
    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private ObjectMapper objectMapper; // ✅ Ensure correct JSON handling
    /**
     * Endpoint to add a single serial number and IMEI with deviceId.
     */

    @PostMapping("/add-single")
    public ResponseEntity<Map<String, String>> addSingleVehicle(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("📌 Received request to add single vehicle: {}", payload);

            // Ensure all keys exist in the payload before fetching them
            if (!payload.containsKey("serialNo") || !payload.containsKey("imei") || !payload.containsKey("dealerId")) {
                throw new IllegalArgumentException("❌ Missing required parameters: serialNo, imei, dealerId");
            }

            String serialNo = payload.get("serialNo") != null ? payload.get("serialNo").toString() : null;
            String imei = payload.get("imei") != null ? payload.get("imei").toString() : null;
            Long dealerId = payload.get("dealerId") != null ? Long.parseLong(payload.get("dealerId").toString()) : null;

            if (serialNo == null || imei == null || dealerId == null) {
                throw new IllegalArgumentException("❌ serialNo, imei, or dealerId cannot be null.");
            }

            // Call Service to add vehicle
            vehicleService.addVehicle(serialNo, imei, dealerId);

            logger.info("✅ Successfully added single vehicle with Serial No: {}", serialNo);
            response.put("success", "true");
            response.put("message", "Single vehicle added successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error adding single vehicle", e);
            response.put("success", "false");
            response.put("message", "Failed to add single vehicle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    

    // ✅ Add Dual Vehicles
    /**
     * ✅ Add Dual Serial Numbers with Dealer
     */
    /**
     * ✅ Add Dual Serial Numbers with Dealer
     */
    @PostMapping("/add-multiple")
    public ResponseEntity<Map<String, Object>> addMultipleDevices(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ Extract dealerId
            Object dealerIdObj = payload.get("dealerId");
            if (dealerIdObj == null) {
                throw new IllegalArgumentException("Dealer ID is required");
            }
            Long dealerId = (dealerIdObj instanceof Number)
                    ? ((Number) dealerIdObj).longValue()
                    : Long.parseLong(dealerIdObj.toString());

            Dealer dealer = dealerRepository.findById(dealerId)
                    .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));

            // ✅ Extract and convert devices list
            List<Map<String, Object>> devices = (List<Map<String, Object>>) payload.get("devices");
            if (devices == null || devices.isEmpty()) {
                throw new IllegalArgumentException("Device list is empty");
            }

            for (Map<String, Object> deviceMap : devices) {
                String serialNo = (String) deviceMap.get("serialNo");
                String imei = (String) deviceMap.get("imei");

                if (serialNo == null || imei == null || serialNo.isBlank() || imei.isBlank()) {
                    continue; // Skip incomplete entries
                }

                Vehicle vehicle = new Vehicle();
                vehicle.setSerialNo(serialNo);
                vehicle.setImei(imei);
                vehicle.setDealer(dealer);

                vehicleRepository.save(vehicle);
            }

            response.put("success", "true");
            response.put("message", "Devices added successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", "false");
            response.put("message", "Error adding devices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    


    /**
     * Fetch all dealers with their names and IDs.
     */
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
    
    
    
    

    
    /*
    @GetMapping("/{serialNo}")
    public ResponseEntity<Map<String, String>> getVehicleBySerialNo(@PathVariable String serialNo) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleBySerialNo(serialNo);
        if (vehicle.isPresent()) {
            Vehicle foundVehicle = vehicle.get();
            Map<String, String> response = new HashMap<>();
            response.put("imei", foundVehicle.getImei());
            response.put("serialNo", foundVehicle.getSerialNo());
            response.put("vehicleType", foundVehicle.getVehicleType());
            response.put("technicianName", foundVehicle.getTechnicianName());
            response.put("simNumber", foundVehicle.getSimNumber());
            response.put("dealerName", foundVehicle.getDealerName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
*/
}
