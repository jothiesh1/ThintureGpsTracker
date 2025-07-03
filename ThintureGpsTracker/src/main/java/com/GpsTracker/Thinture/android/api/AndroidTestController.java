package com.GpsTracker.Thinture.android.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/android")
public class AndroidTestController {

    private static final Logger logger = LoggerFactory.getLogger(AndroidTestController.class);

    // ✅ Simple test endpoint to check if Android API is working
    @GetMapping("/test")
    public Map<String, Object> test() {
        logger.info("🧪 Android test endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Android API is working!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("endpoint", "/api/android/test");
        
        return response;
    }

    // ✅ Health check endpoint
    @GetMapping("/health")
    public Map<String, Object> health() {
        logger.info("🏥 Android health check called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "GPS Tracker Android API");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        
        return response;
    }
}