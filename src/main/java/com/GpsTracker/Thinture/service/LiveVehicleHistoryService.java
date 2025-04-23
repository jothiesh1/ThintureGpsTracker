package com.GpsTracker.Thinture.service;
import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleLiveHistoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
@Service
public class LiveVehicleHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(LiveVehicleHistoryService.class);

    @Autowired
    private VehicleLiveHistoryRepository vehicleLiveHistoryRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Fetch all live vehicle locations from the database.
     */
    public List<VehicleLastLocation> getAllLiveLocations() {
        logger.info("[LiveVehicleHistoryService] Fetching all live vehicle locations.");
        return vehicleLiveHistoryRepository.findAllLiveLocations();
    }

    /**
     * Fetch a specific vehicle's last known location by device ID.
     */
    public Optional<VehicleLastLocation> getLiveLocationByDeviceId(String deviceId) {
        logger.info("[LiveVehicleHistoryService] Fetching live location for device ID: {}", deviceId);
        return vehicleLiveHistoryRepository.findByDeviceId(deviceId);
    }

    /**
     * Send all live vehicle locations via WebSocket.
     */
    public void broadcastLiveLocations() {
        List<VehicleLastLocation> locations = vehicleLiveHistoryRepository.findAllLiveLocations();
        
        for (VehicleLastLocation location : locations) {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition(),
                location.getCourse(),
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );

            // Send live location update via WebSocket
            messagingTemplate.convertAndSend("/topic/location-updates", update);
        }
        
        logger.info("[LiveVehicleHistoryService] Broadcasted {} live locations.", locations.size());
    }
}