package com.GpsTracker.Thinture.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.service.HereOAuthTokenService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private HereOAuthTokenService hereOAuthTokenService;
    
    @GetMapping("/here-token")
    public ResponseEntity<Map<String, Object>> testHereToken() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = hereOAuthTokenService.fetchAccessToken();
            response.put("success", true);
            response.put("token", token);
            response.put("tokenLength", token.length());
            response.put("tokenPrefix", token.substring(0, Math.min(20, token.length())) + "...");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}