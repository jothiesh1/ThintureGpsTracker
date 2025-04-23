package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class LiveWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LiveWebSocketController.class);
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Handle request to fetch and broadcast a specific device location
     */
    @MessageMapping("/fetch-location")
    public void fetchAndBroadcastLocation(@Payload String deviceId) {
        logger.info(ANSI_BLUE + "[WebSocket] Manual location fetch request for device: {}" + ANSI_RESET, deviceId);
        
        Optional<VehicleLastLocation> locationOpt = vehicleLastLocationRepository.findByDeviceId(deviceId);
        if (locationOpt.isPresent()) {
            VehicleLastLocation location = locationOpt.get();
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition(),
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
            logger.info(ANSI_GREEN + "[WebSocket] Manually broadcast location for device: {}" + ANSI_RESET, deviceId);
        } else {
            logger.warn("[WebSocket] Device location not found for ID: {}", deviceId);
        }
    }
    
    /**
     * Handle request to fetch and broadcast all vehicle locations
     */
    @MessageMapping("/fetch-all-locations")
    public void fetchAndBroadcastAllLocations() {
        logger.info(ANSI_BLUE + "[WebSocket] Manual fetch request for all locations" + ANSI_RESET);
        
        List<VehicleLastLocation> locations = vehicleLastLocationRepository.findAll();
        
        for (VehicleLastLocation location : locations) {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition() ,
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
        }
        
        logger.info(ANSI_GREEN + "[WebSocket] Broadcast {} locations from database" + ANSI_RESET, locations.size());
    }
}