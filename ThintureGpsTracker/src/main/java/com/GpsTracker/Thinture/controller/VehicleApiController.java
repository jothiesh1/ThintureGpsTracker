package com.GpsTracker.Thinture.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      
    
    
    
    
    
    }
    

    





















