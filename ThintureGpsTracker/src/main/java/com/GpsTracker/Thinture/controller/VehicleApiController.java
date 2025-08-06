package com.GpsTracker.Thinture.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.dto.VehicleDTO;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import com.GpsTracker.Thinture.service.VehicleLastLocationService;
//import com.GpsTracker.Thinture.model.vehicle;
import com.GpsTracker.Thinture.service.VehicleService;

@RestController
@RequestMapping("/api")
public class VehicleApiController {
	 @Autowired
	    private VehicleHistoryService vehicleHistoryService;
	 private static final Logger logger = LoggerFactory.getLogger(VehicleApiController.class);
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleLastLocationService vehicleLastLocationService;
    // API method to return vehicle data as JSON
    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles() {
        return vehicleService.getAllVehicles();
    }
    
    // API method to return the count of all vehicles
    @GetMapping("/vehicles/countAll")
    public ResponseEntity<Long> countAllVehicles() {
        long count = vehicleService.countAllVehicles();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    @GetMapping("/vehicles/last-known")
    public ResponseEntity<List<VehicleLastLocation>> getAllLastKnownLocations() {
        List<VehicleLastLocation> vehicleLastLocations = vehicleLastLocationService.getAllLastKnownLocations();
        return new ResponseEntity<>(vehicleLastLocations, HttpStatus.OK);
    }
	/*
	 * @GetMapping("/api/vehicles/last-known") public List<VehicleLastLocation>
	 * getLastKnownLocations() { return vehicleService.getAllLastKnownLocations(); }
	 */

    // API endpoint to get the last known location by device ID
    @GetMapping("/vehicles/last-known/{deviceId}")
    public Optional<VehicleLastLocation> getLastKnownLocationByDeviceId(@PathVariable String deviceId) {
        return vehicleService.getLastKnownLocationByDeviceId(deviceId);
    }
    //Ui button stop button for map code
    // API method to return the count of all vehicles

    // Search vehicle by device ID
    @GetMapping("/countByStatus")
    public ResponseEntity<Long> countVehiclesByStatus(@RequestParam String status) {
        long count = vehicleLastLocationService.countVehiclesByStatus(status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
        
        
        //Ui button stop button for map code
        // API method to return the count of all vehicles
      
    
    // ✅ Fetch All Vehicles (Returns List of VehicleDTO)
    
    @GetMapping("/details")
    public ResponseEntity<List<VehicleDTO>> getAllVehicleDetails() {
        logger.debug("CHECKING FETCHING DATA - Vehicle: ");
        
        List<VehicleDTO> vehicles = vehicleService.getAllVehicless();

        // ✅ Add this block to log installation and renewal dates for each vehicle
        for (VehicleDTO vehicle : vehicles) {
            logger.info("Vehicle DeviceID={} | InstallationDate={} | RenewalDate={}", 
                vehicle.getDeviceID(), 
                vehicle.getInstallationDate(), 
                vehicle.getRenewalDate());
        }

        return ResponseEntity.ok(vehicles);
    }

    
    
    
    // ✅ Fetch Single Vehicle by deviceID with Logging
   
    
    // ✅ Fetch Single Vehicle by DeviceID with Logger
    @GetMapping("/details/{deviceId}")
    public ResponseEntity<VehicleDTO> getVehicleDetails(@PathVariable String deviceId) {
        logger.info("📡 Received request to fetch vehicle details for Device ID: {}", deviceId);

        Optional<VehicleDTO> vehicle = vehicleService.getVehicleByDeviceID1(deviceId);

        if (vehicle.isPresent()) {
            logger.info("✅ Successfully retrieved vehicle details for Device ID: {}", deviceId);
            return ResponseEntity.ok(vehicle.get());
        } else {
            logger.warn("❌ No vehicle found with Device ID: {}", deviceId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/delete/{deviceID}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String deviceID) {
        logger.info("🗑️ API request to delete Vehicle and LastLocation for deviceID={}", deviceID);
        try {
            vehicleService.deleteVehicleAndLastLocation(deviceID);
            logger.info("✅ Successfully deleted both records for deviceID={}", deviceID);
            return ResponseEntity.ok("Vehicle and last-known location deleted successfully.");
        } catch (Exception e) {
            logger.error("❌ Error deleting records for deviceID={}: {}", deviceID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to delete vehicle or last-known location.");
        }
    }

    
    @GetMapping("/vehicle/type")
    public ResponseEntity<String> getVehicleTypeByDeviceId(@RequestParam String deviceId) {
        logger.info("Fetching vehicle type for device ID: {}", deviceId);
        Optional<Vehicle> optionalVehicle = vehicleService.getVehicleByDeviceID(deviceId);
        if (optionalVehicle.isPresent()) {
            return ResponseEntity.ok(optionalVehicle.get().getVehicleType());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UNKNOWN");
        }
    }


    @PutMapping("/update/{deviceID}")
    public ResponseEntity<?> updateVehicle(@PathVariable String deviceID, @RequestBody VehicleDTO dto) {
        logger.info("Update request for deviceID: {}", deviceID);
        logger.debug("Incoming DTO: {}", dto);

        Optional<Vehicle> optionalVehicle = vehicleService.getVehicleByDeviceID(deviceID);

        if (optionalVehicle.isEmpty()) {
            logger.warn("Vehicle not found for deviceID: {}", deviceID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
        }

        Vehicle vehicle = optionalVehicle.get();
        logger.debug("Before Update - Vehicle: {}", vehicle);

        // Copy fields
        vehicle.setOwnerName(dto.getOwnerName());
        vehicle.setImei(dto.getImei());
        vehicle.setSimNumber(dto.getSimNumber());
        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setModel(dto.getModel());

        vehicleService.save(vehicle);
        logger.info("Vehicle updated successfully: {}", deviceID);

        return ResponseEntity.ok(dto);
    }


    }
    

    





















