package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class LiveWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LiveWebSocketController.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/fetch-my-vehicles")
    public void fetchMyVehicles(@Payload Map<String, String> payload) {
        String userType = payload.get("userType");
        Long userId = Long.parseLong(payload.get("userId"));

        logger.info("[WebSocket] 🌐 Fetching vehicles for type='{}' and ID={}", userType, userId);

        List<Vehicle> vehicles = switch (userType.toLowerCase()) {
            case "admin" -> vehicleRepository.findByAdminId(userId);
            case "dealer" -> vehicleRepository.findByDealerId(userId);
            case "client" -> vehicleRepository.findByClientId(userId);
            case "user" -> vehicleRepository.findByUserId(userId);
            case "driver" -> vehicleRepository.findByDriverId(userId);
            default -> {
                logger.warn("❌ Invalid userType='{}' for ID={}", userType, userId);
                yield List.of();
            }
        };

        int sentCount = 0;

        for (Vehicle vehicle : vehicles) {
            String deviceId = vehicle.getDeviceID();
            if (deviceId == null || deviceId.isBlank()) {
                logger.debug("⚠️ Skipping vehicle with empty deviceID");
                continue;
            }

            // Strict ownership enforcement
            boolean isOwner = switch (userType.toLowerCase()) {
                case "admin" -> userId.equals(vehicle.getAdmin_id());
                case "dealer" -> userId.equals(vehicle.getDealer_id());
                case "client" -> userId.equals(vehicle.getClient_id());
                case "user" -> userId.equals(vehicle.getUser_id());
                case "driver" -> userId.equals(vehicle.getDriver_id());
                default -> false;
            };

            if (!isOwner) {
                logger.warn("⛔ Unauthorized access attempt: userType='{}', userId={}, deviceID='{}'",
                        userType, userId, deviceId);
                continue;
            }

            vehicleLastLocationRepository.findByDeviceId(deviceId).ifPresentOrElse(location -> {
                LocationUpdate update = new LocationUpdate(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getDeviceId(),
                        location.getTimestamp().toString(),
                        location.getSpeed(),
                        location.getIgnition(),
                        location.getCourse(),
                        location.getVehicleStatus(),
                        "", ""
                );
                messagingTemplate.convertAndSend("/topic/location-updates", update);
                logger.info("✅ Sent location for deviceID='{}' to {} {}", deviceId, userType, userId);
            }, () -> {
                logger.warn("⚠️ No last location found for deviceID='{}'", deviceId);
            });

            sentCount++;
        }

        logger.info("[WebSocket] ✅ Finished. Total vehicles broadcasted: {}", sentCount);
    }
}
