package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.service.VehicleHistoryService;

@RestController
@RequestMapping("/api")
public class VehicleHistoryController {

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @PostMapping("/log")
    public ResponseEntity<String> logVehicleData(@RequestParam String deviceID, @RequestBody VehicleHistory historyData) {
        vehicleHistoryService.saveVehicleHistory(historyData);
        return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);
    }
}

