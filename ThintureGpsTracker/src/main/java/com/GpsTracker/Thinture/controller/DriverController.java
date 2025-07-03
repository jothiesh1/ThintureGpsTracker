package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.dto.DriverDTO;
import com.GpsTracker.Thinture.service.DriverService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    @Autowired
    private DriverService driverService;

    // 🔹 Add new driver
    @PostMapping
    public ResponseEntity<Driver> addDriver(@RequestBody DriverDTO dto) {
        try {
            // 🔐 Get authenticated user info
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
                logger.warn("❌ Unauthorized access - invalid user principal.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long userId = userDetails.getId();
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            logger.info("👤 Driver add request by: {} (ID: {}, Role: {})", email, userId, role);
            logger.info("🚗 Creating new driver with data: {}", dto);

            // 💾 Save driver
            Driver saved = driverService.addDriverFromDTO(dto);

            logger.info("✅ Driver created with ID: {}", saved.getId());
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            logger.error("❌ Error creating driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }


    // 🔹 Get all drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // 🔹 Get driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Update driver
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody DriverDTO dto) {
        try {
            Driver updated = driverService.updateDriverFromDTO(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.warn("Driver not found for update: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Delete driver
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 For dropdown autocomplete (name + id)
    @GetMapping("/dropdown")
    public ResponseEntity<List<Map<String, Object>>> getDriversForDropdown() {
        try {
            List<Map<String, Object>> driverList = new ArrayList<>();
            for (Driver driver : driverService.getAllDrivers()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", driver.getId());
                map.put("name", driver.getFullName());
                driverList.add(map);
            }
            return ResponseEntity.ok(driverList);
        } catch (Exception e) {
            logger.error("❌ Failed to load drivers for dropdown", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleDriverStatus(@PathVariable Long id) {
        try {
            String result = driverService.toggleDriverStatus(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
