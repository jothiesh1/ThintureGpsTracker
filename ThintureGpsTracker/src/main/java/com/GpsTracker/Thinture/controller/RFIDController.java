package com.GpsTracker.Thinture.controller;
import com.GpsTracker.Thinture.dto.RFIDDetailsDTO;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.LiveVehicleHistoryService;
import com.GpsTracker.Thinture.service.RFIDService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rfid")
public class RFIDController {

	
	
    private static final Logger logger = LoggerFactory.getLogger(RFIDController.class);

    @Autowired
    private RFIDService rfidService;

    @PostMapping("/registerForDealer")
    public ResponseEntity<String> registerRFIDs(
            @RequestParam Long dealerId,
            @RequestBody List<String> rfidCodes) {

        logger.info("[RFIDController] Received {} RFID(s) for dealerId={}", rfidCodes.size(), dealerId);

        rfidService.saveRFIDs(rfidCodes, dealerId);

        logger.info("[RFIDController] Successfully processed RFID registration");
        return ResponseEntity.ok("RFIDs registered successfully!");
    }
    
    
    
    
    
    
    
    // Endpoint for registering RFIDs for a client
    @PostMapping("/registerForClient")
    public ResponseEntity<String> registerRFIDsForClient(
            @RequestParam Long clientId,  // Use clientId instead of dealerId
            @RequestBody List<String> rfidCodes) {

        logger.info("[RFIDController] Received {} RFID(s) for clientId={}", rfidCodes.size(), clientId);

        // Call the service method for saving RFIDs linked to the client
        try {
            rfidService.saveRFIDsForClient(rfidCodes, clientId);
            logger.info("[RFIDController] Successfully processed RFID registration for clientId={}", clientId);
            return ResponseEntity.ok("RFIDs registered successfully for client!");
        } catch (RuntimeException e) {
            logger.error("[RFIDController] Error registering RFIDs for clientId={}", clientId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register RFIDs: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/all")
    public ResponseEntity<List<RFIDDetailsDTO>> getAllRFIDsWithDetails() {
        List<RFIDDetailsDTO> rfidList = rfidService.getAllRFIDDetails();
        if (rfidList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        logger.info("[RFIDController] Successfully FETCHING RFID");
        return ResponseEntity.ok(rfidList);
    }
    
    
}
