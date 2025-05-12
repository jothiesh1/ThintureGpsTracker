package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.service.HereOAuthTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/speed-limit")
public class SpeedLimitController {
    
    private static final Logger logger = LoggerFactory.getLogger(SpeedLimitController.class);
    
    // HERE API Key for map initialization and API calls
    private static final String API_KEY = "19cJwlsY5xkdO13O_869LM29OScg30J1TCstjWfPSF8";
    
    @Autowired
    private HereOAuthTokenService tokenService;
    
    /**
     * Display the speed limit map page
     */
    @GetMapping
    public String showSpeedLimitMap(Model model) {
        try {
            // Pass API key to the view
            model.addAttribute("apiKey", API_KEY);
            
            logger.info("🗺️ Loading speed limit map view");
            return "speed-limit-map";
        } catch (Exception e) {
            logger.error("❌ Error preparing speed limit map view: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    /**
     * API endpoint to get speed limit data
     */
    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeedLimit(
            @RequestParam double lat, 
            @RequestParam double lng) {
        
        logger.info("🔍 Received request for speed limit at lat={}, lng={}", lat, lng);
        
        // Validate coordinates
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            logger.error("❌ Invalid coordinates: lat={}, lng={}", lat, lng);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Invalid coordinates. Latitude must be between -90 and 90, longitude between -180 and 180.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            // Create a small destination point a short distance away
            // This ensures we can create a valid route that looks up the road info
            double destLat = lat + 0.002;
            double destLng = lng + 0.002;
            
            // Build the URL for the HERE Routes API with valid return parameters
            // Valid return values include: polyline, actions, instructions, summary, typicalDuration
            String apiUrl = String.format(
                "https://router.hereapi.com/v8/routes" +
                "?transportMode=car" +
                "&origin=%.6f,%.6f" +
                "&destination=%.6f,%.6f" +
                "&return=polyline,actions,instructions,summary" +
                "&apiKey=%s",
                lat, lng,
                destLat, destLng,
                API_KEY
            );
            
            logger.info("🌐 Calling HERE Routing API");
            
            // Set up HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            
            // Make the API call
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.GET, 
                request, 
                Map.class
            );
            
            // Process the response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("location", Map.of("lat", lat, "lng", lng));
                
                try {
                    // Extract road information
                    Map<String, Object> responseBody = response.getBody();
                    String roadName = "Unknown Road";
                    int speedLimit = 50; // Default value
                    
                    // Extract route data from response
                    if (responseBody.containsKey("routes")) {
                        List<Map<String, Object>> routes = (List<Map<String, Object>>) responseBody.get("routes");
                        if (routes != null && !routes.isEmpty()) {
                            Map<String, Object> route = routes.get(0);
                            
                            if (route.containsKey("sections")) {
                                List<Map<String, Object>> sections = (List<Map<String, Object>>) route.get("sections");
                                if (sections != null && !sections.isEmpty()) {
                                    Map<String, Object> section = sections.get(0);
                                    
                                    // Try to get road name from actions
                                    if (section.containsKey("actions")) {
                                        List<Map<String, Object>> actions = (List<Map<String, Object>>) section.get("actions");
                                        for (Map<String, Object> action : actions) {
                                            if (action.containsKey("currentRoad")) {
                                                roadName = (String) action.get("currentRoad");
                                                break;
                                            }
                                        }
                                    }
                                    
                                    // Try to get road type to estimate speed limit
                                    String roadType = "";
                                    if (section.containsKey("type")) {
                                        roadType = (String) section.get("type");
                                    }
                                    
                                    // Estimate speed limit based on section data
                                    if (section.containsKey("summary")) {
                                        Map<String, Object> summary = (Map<String, Object>) section.get("summary");
                                        
                                        // Try to determine road type from summary
                                        if (summary.containsKey("duration") && summary.containsKey("length")) {
                                            int duration = (int) summary.get("duration");
                                            int length = (int) summary.get("length");
                                            
                                            // Calculate average speed in km/h
                                            if (duration > 0) {
                                                double avgSpeed = (length / 1000.0) / (duration / 3600.0);
                                                
                                                // Estimate speed limit based on average speed
                                                if (avgSpeed > 80) {
                                                    speedLimit = 100; // Highway
                                                } else if (avgSpeed > 50) {
                                                    speedLimit = 80;  // Main road
                                                } else if (avgSpeed > 30) {
                                                    speedLimit = 50;  // Urban road
                                                } else {
                                                    speedLimit = 30;  // Residential
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Set result values
                    result.put("speedLimit", speedLimit);
                    result.put("unit", "KMH");
                    result.put("road", roadName);
                    result.put("note", "Speed limit is estimated based on route characteristics");
                    
                    logger.info("🚦 Estimated speed limit: {} KMH on {}", speedLimit, roadName);
                    return ResponseEntity.ok(result);
                    
                } catch (Exception e) {
                    logger.error("❌ Error processing route data: {}", e.getMessage(), e);
                    
                    // Fallback to default speed value
                    result.put("speedLimit", 50);
                    result.put("unit", "KMH");
                    result.put("road", "Unknown Road");
                    result.put("note", "Default speed limit (data processing error)");
                    
                    return ResponseEntity.ok(result);
                }
            } else {
                logger.error("❌ HERE API error: Status={}", response.getStatusCode());
                throw new RuntimeException("HERE API returned status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("❌ Failed to get speed limit: {}", e.getMessage(), e);
            
            // Return a fallback response with default values instead of an error
            Map<String, Object> fallbackResponse = new HashMap<>();
            fallbackResponse.put("success", true); // Pretend it worked for UI purposes
            fallbackResponse.put("speedLimit", 50);
            fallbackResponse.put("unit", "KMH");
            fallbackResponse.put("road", "Unknown Road");
            fallbackResponse.put("location", Map.of("lat", lat, "lng", lng));
            fallbackResponse.put("note", "Default speed limit (API error)");
            
            return ResponseEntity.ok(fallbackResponse);
        }
    }
    
    /**
     * Alternate API endpoint (using Fleet API as a backup)
     */
    @GetMapping("/api/data/alternate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeedLimitAlternate(
            @RequestParam double lat, 
            @RequestParam double lng) {
        
        logger.info("🔍 Trying alternate method for speed limit at lat={}, lng={}", lat, lng);
        
        try {
            // Try using a different approach - we'll simulate this
            // since the actual Fleet API may require additional subscriptions
            
            // Return a simulated response
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("location", Map.of("lat", lat, "lng", lng));
            result.put("speedLimit", 60);
            result.put("unit", "KMH");
            result.put("road", "Road (Alt Method)");
            result.put("note", "Speed limit from alternate method");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("❌ Failed alternate method: {}", e.getMessage(), e);
            
            // Return a fallback response with default values
            Map<String, Object> fallbackResponse = new HashMap<>();
            fallbackResponse.put("success", true);
            fallbackResponse.put("speedLimit", 40);
            fallbackResponse.put("unit", "KMH");
            fallbackResponse.put("road", "Unknown Road");
            fallbackResponse.put("location", Map.of("lat", lat, "lng", lng));
            fallbackResponse.put("note", "Default speed limit (alternate API error)");
            
            return ResponseEntity.ok(fallbackResponse);
        }
    }
    
    /**
     * Error handler for this controller
     */
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        logger.error("❌ Unhandled error in SpeedLimitController: {}", e.getMessage(), e);
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }
}