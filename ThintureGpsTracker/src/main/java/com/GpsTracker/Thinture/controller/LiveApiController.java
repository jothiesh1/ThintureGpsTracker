package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.LiveVehicleHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/live")
@CrossOrigin(origins = "*") // Allow cross-origin requests
public class LiveApiController {

    private static final Logger logger = LoggerFactory.getLogger(LiveApiController.class);

    @Autowired
    private LiveVehicleHistoryService liveVehicleHistoryService;

    /**
     * Test endpoint to verify API is working
     */
    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        logger.info("[API] Test endpoint accessed");
        return ResponseEntity.ok("Live API is working!");
    }

    /**
     * Get all live vehicle locations
     */
    @GetMapping("/locations")
    public ResponseEntity<List<VehicleLastLocation>> getAllLiveLocations() {
        try {
            logger.info("[API] Fetching all live vehicle locations");
            List<VehicleLastLocation> locations = liveVehicleHistoryService.getAllLiveLocations();
            
            if (locations.isEmpty()) {
                logger.warn("[API] No live locations found!");
            } else {
                logger.info("[API] Returned {} live locations", locations.size());
            }
            
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            logger.error("[API] Error fetching live locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get live location of a specific vehicle by device ID
     */
    @GetMapping("/locations/{deviceId}")
    public ResponseEntity<VehicleLastLocation> getLiveLocationByDeviceId(@PathVariable String deviceId) {
        try {
            logger.info("[API] Fetching live location for device ID: {}", deviceId);
            Optional<VehicleLastLocation> location = liveVehicleHistoryService.getLiveLocationByDeviceId(deviceId);
            
            if (location.isPresent()) {
                logger.info("[API] Found live location for device: {}", deviceId);
                return ResponseEntity.ok(location.get());
            } else {
                logger.warn("[API] No live location found for device: {}", deviceId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("[API] Error fetching live location for device {}: {}", deviceId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Broadcast all live locations via WebSocket
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastLiveLocations() {
        try {
            logger.info("[API] Broadcasting all live vehicle locations via WebSocket");
            
            // ✅ Fix: Correctly call the broadcast method instead of just fetching data.
            liveVehicleHistoryService.broadcastLiveLocations();
            
            return ResponseEntity.ok("Live locations broadcasted successfully!");
        } catch (Exception e) {
            logger.error("[API] Error broadcasting live locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error broadcasting live locations: " + e.getMessage());
        }
    }
}
