package com.GpsTracker.Thinture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.GpsTracker.Thinture.model.vehicle;
import com.GpsTracker.Thinture.service.VehicleService;
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
}