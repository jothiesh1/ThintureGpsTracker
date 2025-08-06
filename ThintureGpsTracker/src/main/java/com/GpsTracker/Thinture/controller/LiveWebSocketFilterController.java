package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.service.UserTypeFilterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class LiveWebSocketFilterController {

    private static final Logger logger = LoggerFactory.getLogger(LiveWebSocketFilterController.class);

    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserTypeFilterService userTypeFilterService;

    @GetMapping("/my-devices")
    public ResponseEntity<List<String>> getMyDevices() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            logger.warn("❌ [Auth] No authentication found – rejecting /my-devices request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        var userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) {
            logger.warn("❌ [Auth] No userInfo found for email: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String role = userInfo.getRole().toLowerCase();
        Long id = userInfo.getId();

        logger.info("📥 [Device Fetch] {} (role={}, id={}) requested their devices", email, role, id);

        List<String> deviceIDs = switch (role) {
            case "dealer" -> vehicleRepository.findDeviceIDsByDealerId(id);
            case "admin" -> vehicleRepository.findDeviceIDsByAdminId(id);
            case "client" -> vehicleRepository.findDeviceIDsByClientId(id);
            case "user" -> vehicleRepository.findDeviceIDsByUserId(id);
            default -> {
                logger.error("❗ [ERROR] Unexpected role '{}' for user {}", role, email);
                throw new IllegalStateException("Unexpected value: " + role);
            }
        };

        logger.info("✅ [Device Fetch] {} has {} devices assigned", email, deviceIDs.size());
        return ResponseEntity.ok(deviceIDs);
    }

    @GetMapping("/session-role")
    public ResponseEntity<Map<String, Object>> getLoggedUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            logger.warn("❌ [Session] No authentication found – rejecting /session-role request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        var userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) {
            logger.warn("❌ [Session] No userInfo found for session email: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        logger.info("👤 [Session] {} is logged in as {} (ID={})", email, userInfo.getRole(), userInfo.getId());

        return ResponseEntity.ok(Map.of(
            "username", email,
            "authorities", auth.getAuthorities(),
            "id", userInfo.getId(),
            "role", userInfo.getRole().toLowerCase()
        ));
    }
}
