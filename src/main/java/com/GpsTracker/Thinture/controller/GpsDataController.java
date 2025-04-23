package com.GpsTracker.Thinture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.service.GpsDataService;

@RestController
@RequestMapping("/gps")
public class GpsDataController {
    @Autowired
    private GpsDataService gpsDataService;

//    @PostMapping("/add")
//    public GpsData addOrUpdateGpsData(@RequestBody GpsData gpsData) {
//        return gpsDataService.saveOrUpdateGpsData(gpsData);
//    }

    @DeleteMapping("/delete/{deviceID}")
    public void deleteGpsData(@PathVariable String deviceID) {
        gpsDataService.deleteByDeviceID(deviceID);
    }
    @GetMapping("/last-locations")
    public List<GpsData> getLastKnownLocations() {
        return gpsDataService.findAllLastGpsData();
    }
    @GetMapping("/devices/locations")
    public ResponseEntity<List<GpsData>> getAllDeviceLocations() {
        List<GpsData> deviceLocations = gpsDataService.findAllLatestDeviceLocations();
        return ResponseEntity.ok(deviceLocations);
    }

 // Endpoint to handle incoming GPS data and save it automatically 
    @PostMapping("/add")
    public ResponseEntity<GpsData> addOrUpdateGpsData(@RequestBody GpsData gpsData) {
        GpsData savedData = gpsDataService.saveOrUpdateGpsData(gpsData);
        return ResponseEntity.ok(savedData);
    }
//
}
