package com.GpsTracker.Thinture.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.model.vehicle;
import com.GpsTracker.Thinture.service.VehicleService;

@RestController
@RequestMapping("/api")
public class VehicleApiController {

    @Autowired
    private VehicleService vehicleService;

    // API method to return vehicle data as JSON
    @GetMapping("/vehicles")
    public List<vehicle> getVehicles() {
        return vehicleService.getAllVehicles();
    }
 // API method to return the count of all vehicles
    @GetMapping("/vehicles/countAll")
    public ResponseEntity<Long> countAllVehicles() {
        long count = vehicleService.countAllVehicles();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

	/*
	 * @GetMapping("/vehicles/newThisMonth") public ResponseEntity<Long>
	 * countNewVehiclesThisMonth() { long count =
	 * vehicleService.countNewVehiclesThisMonth(); return new
	 * ResponseEntity<>(count, HttpStatus.OK); }
	 */
    // API method to search by deviceID
    // API method to search by deviceID
    @GetMapping("/vehicles/search")
    public ResponseEntity<vehicle> searchVehicleByDeviceID(@RequestParam String deviceID) {
        Optional<vehicle> vehicle = vehicleService.findByDeviceID(deviceID);
        if (vehicle.isPresent()) {
            return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
