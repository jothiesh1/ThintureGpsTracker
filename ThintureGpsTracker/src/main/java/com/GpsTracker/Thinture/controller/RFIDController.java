package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.RFIDDetailsDTO;
import com.GpsTracker.Thinture.service.RFIDService;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> registerRFIDs(
            @RequestParam Long dealerId,
            @RequestBody List<String> rfidCodes) {

        logger.info("[RFIDController] Received {} RFID(s) for dealerId={}", rfidCodes.size(), dealerId);

        try {
            Map<String, Object> result = rfidService.saveRFIDs(rfidCodes, dealerId);
            logger.info("[RFIDController] Successfully processed RFID registration");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("[RFIDController] Error registering RFIDs for dealerId={}", dealerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Failed to register RFIDs: " + e.getMessage()));
        }
    }

    @PostMapping("/registerForClient")
    public ResponseEntity<Map<String, Object>> registerRFIDsForClient(
            @RequestParam Long clientId,
            @RequestBody List<String> rfidCodes) {

        logger.info("[RFIDController] Received {} RFID(s) for clientId={}", rfidCodes.size(), clientId);

        try {
            Map<String, Object> result = rfidService.saveRFIDsForClient(rfidCodes, clientId);
            logger.info("[RFIDController] Successfully processed RFID registration for clientId={}", clientId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("[RFIDController] Error registering RFIDs for clientId={}", clientId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Failed to register RFIDs: " + e.getMessage()));
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
    
 // Add this method to your existing RFIDController class:

    @GetMapping("/codes")
    public ResponseEntity<List<String>> getAllRFIDCodes() {
        try {
            List<String> rfidCodes = rfidService.getAllRFIDCodes();
            logger.info("[RFIDController] Successfully fetched {} RFID codes", rfidCodes.size());
            return ResponseEntity.ok(rfidCodes);
        } catch (Exception e) {
            logger.error("[RFIDController] Error fetching RFID codes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}