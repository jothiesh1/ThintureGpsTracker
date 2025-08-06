package com.GpsTracker.Thinture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Configuration class for RestTemplate used by GeocodingService
 * to make HTTP calls to OpenStreetMap Nominatim API
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Create RestTemplate bean with appropriate timeouts for geocoding API calls
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Set timeouts for OSM API calls
        factory.setConnectTimeout(10000);    // 10 seconds connection timeout
        factory.setReadTimeout(30000);       // 30 seconds read timeout
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Optional: Add request interceptor for logging
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // Log API calls (optional - can be removed if not needed)
            System.out.println("🌐 [HTTP] Calling: " + request.getURI());
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
}