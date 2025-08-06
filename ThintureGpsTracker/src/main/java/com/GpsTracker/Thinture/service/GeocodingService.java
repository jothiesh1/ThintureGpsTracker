package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.CoordinatesCache;
import com.GpsTracker.Thinture.repository.CoordinatesCacheRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GeocodingService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
    
    // OpenStreetMap Nominatim API endpoint
    private static final String OSM_GEOCODING_URL = 
        "https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}&zoom=18&addressdetails=1";
    
    // ANSI color codes for console logging
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    
    @Autowired
    private CoordinatesCacheRepository coordinatesCacheRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Check if address is already cached for given coordinates
     */
    public boolean isAddressCached(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        
        // Check exact coordinates
        Optional<CoordinatesCache> exactMatch = 
            coordinatesCacheRepository.findByLatitudeAndLongitude(latitude, longitude);
        
        if (exactMatch.isPresent()) {
            return true;
        }
        
        // Check rounded coordinates (nearby locations)
        Optional<CoordinatesCache> roundedMatch = 
            coordinatesCacheRepository.findByRoundedCoordinates(latitude, longitude);
        
        return roundedMatch.isPresent();
    }

    /**
     * Main method to get address for coordinates with caching
     * Now checks for empty addresses and fills them
     */
    public String getAddressFromCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            logger.warn("{}[GEOCODING] Null coordinates provided{}", ANSI_YELLOW, ANSI_RESET);
            return "Invalid Coordinates";
        }
        
        try {
            // Step 1: Check exact coordinates cache first
            Optional<CoordinatesCache> cachedResult = 
                coordinatesCacheRepository.findByLatitudeAndLongitude(latitude, longitude);
            
            if (cachedResult.isPresent()) {
                CoordinatesCache cached = cachedResult.get();
                
                // Check if address is empty (coordinates saved but address not fetched yet)
                if (cached.getAddress() == null || cached.getAddress().trim().isEmpty()) {
                    logger.info("{}[CACHE] Found coordinates {}, {} but address is empty - fetching from API{}",
                        ANSI_YELLOW, latitude, longitude, ANSI_RESET);
                    
                    // Fetch address from API and update the record
                    String address = fetchAddressFromOSM(latitude, longitude);
                    cached.setAddress(address);
                    coordinatesCacheRepository.save(cached);
                    
                    logger.info("{}[CACHE UPDATE] Updated address for {}, {} => {}{}",
                        ANSI_GREEN, latitude, longitude, address, ANSI_RESET);
                    
                    return address;
                } else {
                    // Address already exists - return from cache
                    logger.info("{}[CACHE HIT] Found complete record for {}, {} => {}{}",
                        ANSI_GREEN, latitude, longitude, cached.getAddress(), ANSI_RESET);
                    return cached.getAddress();
                }
            }
            
            // Step 2: Check rounded coordinates (for nearby locations)
            Optional<CoordinatesCache> roundedResult = 
                coordinatesCacheRepository.findByRoundedCoordinates(latitude, longitude);
            
            if (roundedResult.isPresent() && 
                roundedResult.get().getAddress() != null && 
                !roundedResult.get().getAddress().trim().isEmpty()) {
                
                logger.info("{}[CACHE HIT ROUNDED] Found nearby match for {}, {} => {}{}",
                    ANSI_CYAN, latitude, longitude, roundedResult.get().getAddress(), ANSI_RESET);
                return roundedResult.get().getAddress();
            }
            
            // Step 3: Complete cache miss - call OSM API and save new record
            logger.info("{}[CACHE MISS] Calling OSM API for {}, {}{}",
                ANSI_BLUE, latitude, longitude, ANSI_RESET);
            
            String address = fetchAddressFromOSM(latitude, longitude);
            
            // Step 4: Save to cache (new record with address)
            saveToCache(latitude, longitude, address);
            
            return address;
            
        } catch (Exception e) {
            logger.error("{}[ERROR] Failed to get address for {}, {}: {}{}",
                ANSI_RED, latitude, longitude, e.getMessage(), ANSI_RESET, e);
            return "Address Lookup Failed";
        }
    }
    
    /**
     * Fetch address from OpenStreetMap Nominatim API
     */
    private String fetchAddressFromOSM(Double latitude, Double longitude) {
        try {
            // Add rate limiting to respect OSM usage policy
            Thread.sleep(1000); // 1 second delay between requests
            
            String url = OSM_GEOCODING_URL.replace("{lat}", latitude.toString())
                                         .replace("{lon}", longitude.toString());
            
            logger.info("{}[OSM API] Calling: {}{}",
                ANSI_BLUE, url, ANSI_RESET);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null || response.trim().isEmpty()) {
                logger.warn("{}[OSM API] Empty response for {}, {}{}",
                    ANSI_YELLOW, latitude, longitude, ANSI_RESET);
                return "Address not found";
            }
            
            // Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Check if address found
            if (!jsonNode.has("display_name")) {
                logger.warn("{}[OSM API] No display_name in response for {}, {}{}",
                    ANSI_YELLOW, latitude, longitude, ANSI_RESET);
                return "Address not found";
            }
            
            String address = jsonNode.get("display_name").asText();
            logger.info("{}[OSM API] ✅ Address found: {}{}",
                ANSI_GREEN, address, ANSI_RESET);
            
            return address;
            
        } catch (ResourceAccessException e) {
            logger.error("{}[OSM API] Network timeout for {}, {}: {}{}",
                ANSI_RED, latitude, longitude, e.getMessage(), ANSI_RESET);
            return "Network timeout";
            
        } catch (HttpStatusCodeException e) {
            logger.error("{}[OSM API] HTTP error {} for {}, {}: {}{}",
                ANSI_RED, e.getStatusCode(), latitude, longitude, e.getMessage(), ANSI_RESET);
            return "API Error: " + e.getStatusCode();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("{}[OSM API] Request interrupted for {}, {}{}",
                ANSI_RED, latitude, longitude, ANSI_RESET);
            return "Request interrupted";
            
        } catch (Exception e) {
            logger.error("{}[OSM API] Unexpected error for {}, {}: {}{}",
                ANSI_RED, latitude, longitude, e.getMessage(), ANSI_RESET, e);
            return "API call failed";
        }
    }
    
    /**
     * Save coordinates and address to cache with duplicate protection
     */
    private void saveToCache(Double latitude, Double longitude, String address) {
        try {
            // Check if already exists (double-check to avoid race conditions)
            if (coordinatesCacheRepository.existsByLatitudeAndLongitude(latitude, longitude)) {
                logger.info("{}[CACHE] Coordinates {}, {} already cached, skipping save{}",
                    ANSI_CYAN, latitude, longitude, ANSI_RESET);
                return;
            }
            
            CoordinatesCache cacheEntry = new CoordinatesCache(latitude, longitude, address);
            coordinatesCacheRepository.save(cacheEntry);
            
            logger.info("{}[CACHE SAVE] ✅ Cached {}, {} => {}{}",
                ANSI_GREEN, latitude, longitude, address, ANSI_RESET);
            
        } catch (DataIntegrityViolationException e) {
            // Handle duplicate key constraint violation gracefully
            logger.info("{}[CACHE] Duplicate coordinates {}, {} detected, skipping{}",
                ANSI_CYAN, latitude, longitude, ANSI_RESET);
            
        } catch (Exception e) {
            logger.error("{}[CACHE ERROR] Failed to save {}, {} to cache: {}{}",
                ANSI_RED, latitude, longitude, e.getMessage(), ANSI_RESET, e);
        }
    }
    
    /**
     * Async method to get address (for bulk processing)
     */
    public CompletableFuture<String> getAddressAsync(Double latitude, Double longitude) {
        return CompletableFuture.supplyAsync(() -> getAddressFromCoordinates(latitude, longitude));
    }
    
    /**
     * Get cache statistics
     */
    public long getCacheSize() {
        return coordinatesCacheRepository.countCachedCoordinates();
    }
    
    /**
     * Cleanup old cache entries (optional maintenance)
     */
    public void cleanupOldEntries(int daysOld) {
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);
        java.sql.Timestamp cutoffDate = new java.sql.Timestamp(cutoffTime);
        
        try {
            coordinatesCacheRepository.deleteOldEntries(cutoffDate);
            logger.info("{}[CACHE CLEANUP] Removed entries older than {} days{}",
                ANSI_YELLOW, daysOld, ANSI_RESET);
        } catch (Exception e) {
            logger.error("{}[CACHE CLEANUP] Failed to cleanup old entries: {}{}",
                ANSI_RED, e.getMessage(), ANSI_RESET, e);
        }
    }
}