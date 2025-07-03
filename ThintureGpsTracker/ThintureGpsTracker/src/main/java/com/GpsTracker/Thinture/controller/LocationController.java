package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.GpsTracker.Thinture.service.LocationCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OSM_API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s&accept-language=en";

    private final Map<String, String> locationCache = new HashMap<>();

    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getAddress(@RequestParam double lat, @RequestParam double lon) throws JsonMappingException, JsonProcessingException {
        String cacheKey = lat + "," + lon;

        if (locationCache.containsKey(cacheKey)) {
            return ResponseEntity.ok(Collections.singletonMap("display_name", locationCache.get(cacheKey)));
        }

        try {
            String url = String.format(OSM_API_URL, lat, lon);
            System.out.println("📍 Fetching Address: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("❌ API Error: " + response.getStatusCode());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Collections.singletonMap("display_name", "⚠️ External API Error"));
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.getBody());

            String address = jsonResponse.has("display_name") ? jsonResponse.get("display_name").asText() : "📍 Address not found";
            locationCache.put(cacheKey, address);

            return ResponseEntity.ok(Collections.singletonMap("display_name", address));
        } catch (RestClientException e) {
            System.err.println("❌ Error fetching location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("display_name", "⚠️ Unable to fetch location. Try again later."));
        }
    }
}
