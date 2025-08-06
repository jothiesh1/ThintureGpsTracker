package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.service.GeocodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 🌍 Address Controller - Simple endpoint for "Get Address" button
 */
@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class AddressController {
    
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    
    @Autowired
    private GeocodingService geocodingService;
    
    /**
     * 🎯 Main endpoint: Get address for coordinates (Frontend "Get Address" button)
     * URL: GET /api/address/coordinates?lat=12.9716&lon=77.5946
     */
    @GetMapping("/coordinates")
    public ResponseEntity<Map<String, Object>> getAddress(
            @RequestParam("lat") Double latitude,
            @RequestParam("lon") Double longitude) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Input validation
            if (latitude == null || longitude == null) {
                response.put("success", false);
                response.put("message", "Both lat and lon parameters are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("🌍 [API] Address request for coordinates: {}, {}", latitude, longitude);
            
            // Get address (checks cache first, then calls OSM API if needed)
            String address = geocodingService.getAddressFromCoordinates(latitude, longitude);
            
            // Return response
            response.put("success", true);
            response.put("latitude", latitude);
            response.put("longitude", longitude);
            response.put("address", address);
            
            logger.info("🌍 [API] ✅ Address retrieved: {}", address);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("🌍 [API] ❌ Error getting address for {}, {}: {}", 
                    latitude, longitude, e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Failed to get address: " + e.getMessage());
            response.put("latitude", latitude);
            response.put("longitude", longitude);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}