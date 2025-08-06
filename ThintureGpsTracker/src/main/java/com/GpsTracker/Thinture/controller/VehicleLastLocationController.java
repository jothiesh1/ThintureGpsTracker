package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.VehicleLastLocationService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle/last")
public class VehicleLastLocationController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleLastLocationController.class);

    @Autowired
    private VehicleLastLocationService vehicleLastLocationService;

    @GetMapping("/latests-locations/all")
    public ResponseEntity<List<VehicleLastLocation>> getAllLastKnownLocations() {
        logger.info("Request received: Fetch all last known locations.");
        List<VehicleLastLocation> locations = vehicleLastLocationService.getAllLastKnownLocations();
        if (locations.isEmpty()) {
            logger.warn("No vehicle locations found.");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} locations: {}", locations.size(), locations); // Log the entire data
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/latests-locations/{deviceID}")
    public ResponseEntity<VehicleLastLocation> getLastKnownLocation(@PathVariable String deviceID) {
        logger.info("Request received: Fetch last known location for deviceID: {}", deviceID);
        try {
            VehicleLastLocation location = vehicleLastLocationService.getLastKnownLocation(deviceID);
            if (location == null) {
                logger.warn("No location found for deviceID: {}", deviceID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            logger.info("Returning location for deviceID {}: {}", deviceID, location);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            logger.error("Error fetching location for deviceID {}: {}", deviceID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}