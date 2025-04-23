package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.VehicleRenewalDTO;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/vehicles")
public class VehicleRenewalController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRenewalController.class);
    private final VehicleService vehicleService;

    public VehicleRenewalController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

   
  
    /**
     * GET /api/vehicles/renewals?days=N
     * -1 = expired, 0 = all, >0 = due in N days
     */
    @GetMapping("/renewals")
    public ResponseEntity<List<VehicleRenewalDTO>> getRenewals(@RequestParam(defaultValue = "0") int days) {
        logger.info("🔍 GET /api/vehicles/renewals?days={} called", days);
        return ResponseEntity.ok(vehicleService.getRenewalDTOsByDays(days));
    }

    /**
     * PUT /api/vehicles/renew/{id}
     * Marks a vehicle as renewed with remarks
     */

    /**
     * Legacy support
     */
    @GetMapping("/due-renewals")
    public ResponseEntity<List<Vehicle>> getVehiclesDueForRenewal() {
        logger.info("📅 GET /api/vehicles/due-renewals (default 30 days)");
        return ResponseEntity.ok(vehicleService.getDueRenewals(30));
    }
    
    
    
    
    @PutMapping("/renew/{id}")
    public ResponseEntity<String> renewVehicle(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String remarks = payload.getOrDefault("remarks", "Renewed via system");
        double duration = Double.parseDouble(payload.getOrDefault("duration", "1.0")); // Default to 1 year

        logger.info("📩 Renew request received for Vehicle ID: {} | Duration: {} years | Remarks: {}", id, duration, remarks);

        vehicleService.markAsRenewed(id, remarks, duration);
        return ResponseEntity.ok("Vehicle successfully renewed.");
    }

    
    

}
