package com.GpsTracker.Thinture.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class HereOAuthTokenService {
    private static final Logger logger = LoggerFactory.getLogger(HereOAuthTokenService.class);
    
    // API KEY approach - doesn't require token authentication
    private static final String API_KEY = "19cJwlsY5xkdO13O_869LM29OScg30J1TCstjWfPSF8";
    
    // OAuth credentials (if OAuth is needed)
    private static final String CLIENT_ID = "kchQCnuLN6tpJ60e0siy";
    private static final String CLIENT_SECRET = "K-mv-j4q-3zDTYtfrSbhxSBXar7lULggsB4a2PBuG-B60D1g-5vc2QxNjkgvStnmSMEgAMPTi-9FkMboRWsYTA";
    private static final String TOKEN_URL = "https://account.api.here.com/oauth2/token";
    
    /**
     * Returns an access token for HERE API
     * Since we have an API key, we can return it directly instead of
     * going through the OAuth2 flow which is causing 401 errors
     */
    public String fetchAccessToken() {
        // Using API key directly since OAuth is failing with 401
        logger.info("📡 Using HERE API key (bypassing OAuth token)...");
        return API_KEY;
        
        // Uncomment below to try OAuth approach (currently giving 401 error)
        /*
        try {
            logger.info("📡 Requesting HERE OAuth2 token...");
            
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // Create form body with client credentials
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", CLIENT_ID);
            body.add("client_secret", CLIENT_SECRET);
            
            logger.debug("Sending token request to: {}", TOKEN_URL);
            
            // Create HTTP request entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            
            // Make request to HERE OAuth endpoint
            ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
            
            // Process response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                logger.info("✅ HERE Token retrieved successfully");
                return token;
            } else {
                logger.error("❌ Failed to get token: Status={}", response.getStatusCode());
                throw new RuntimeException("Token request failed: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("🔥 Exception while fetching HERE OAuth2 token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to obtain HERE API token: " + e.getMessage(), e);
        }
        */
    }
}