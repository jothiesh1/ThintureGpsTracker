package com.GpsTracker.Thinture.controller;
//Import SLF4J Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.CustomUserDetailsService;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RequestMapping("/api")
@RestController
@RequestMapping("/api/vehicle")
public class VehicleHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleHistoryController.class);

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @GetMapping("/history")
    public ResponseEntity<List<VehicleHistory>> getVehicleHistory(
        @RequestParam("deviceID") String deviceID,
        @RequestParam("month") int month,
        @RequestParam("year") int year) {

        logger.info("Fetching history for deviceID: {}, month: {}, year: {}", deviceID, month, year);
        try {
            List<VehicleHistory> history = vehicleHistoryService.findByDeviceIDMonthYear(deviceID, month, year);
            if (history.isEmpty()) {
                logger.warn("No history found for deviceID: {}", deviceID);
                return ResponseEntity.noContent().build();
            }
            logger.info("Found {} records for deviceID: {}", history.size(), deviceID);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error fetching vehicle history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
    /*
    // Endpoint to save vehicle history
    @PostMapping("/log")
    public ResponseEntity<String> logVehicleData(@RequestParam String deviceID, @RequestBody VehicleHistory historyData) {
        logger.info("Attempting to log vehicle data for deviceID: {}", deviceID);
        try {
            vehicleHistoryService.saveVehicleHistory(historyData);
            logger.info("Successfully logged vehicle data for deviceID: {}", deviceID);
            return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving vehicle data for deviceID: {} - {}", deviceID, e.getMessage(), e);
            return new ResponseEntity<>("Error saving vehicle data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to fetch vehicle history in a date range
    @GetMapping("/history/{deviceID}")
    public ResponseEntity<List<VehicleHistory>> getVehicleHistory(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String toDate
    ) {
        logger.info("Request to fetch history for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);
        try {
            // Parsing date strings to LocalDateTime and converting to Timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime endDate = LocalDateTime.parse(toDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            // Fetching vehicle history
            List<VehicleHistory> historyData = vehicleHistoryService.getVehicleHistory(deviceID, startTimestamp, endTimestamp);
            if (historyData.isEmpty()) {
                logger.warn("No history data found for deviceID: {} between {} and {}", deviceID, fromDate, toDate);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            logger.info("Returning {} records for deviceID: {}", historyData.size(), deviceID);
            return ResponseEntity.ok(historyData);
        } catch (Exception e) {
            logger.error("Error fetching vehicle history for deviceID: {} - {}", deviceID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint to fetch total distance traveled within a date range
    @GetMapping("/distance/{deviceID}")
    public ResponseEntity<Double> getTotalDistance(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String toDate
    ) {
        logger.info("Request to calculate total distance for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);
        try {
            // Parsing date strings to LocalDateTime and converting to Timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime endDate = LocalDateTime.parse(toDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            // Calculating total distance
            double totalDistance = vehicleHistoryService.calculateTotalDistance(deviceID, startTimestamp, endTimestamp);
            logger.info("Total distance for deviceID: {} from {} to {} is {} km", deviceID, fromDate, toDate, totalDistance);
            return ResponseEntity.ok(totalDistance);
        } catch (Exception e) {
            logger.error("Error calculating total distance for deviceID: {} - {}", deviceID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // New: Endpoint to fetch the latest location of the vehicle
    @GetMapping("/latest-location/{deviceID}")
    public ResponseEntity<VehicleHistory> getLatestLocation(@PathVariable("deviceID") String deviceID) {
        logger.info("Request to fetch the latest location for deviceID: {}", deviceID);
        try {
            VehicleHistory latestLocation = vehicleHistoryService.getLatestLocation(deviceID);
            if (latestLocation == null) {
                logger.warn("No location data found for deviceID: {}", deviceID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Returning the latest location for deviceID: {}", deviceID);
            return ResponseEntity.ok(latestLocation);
        } catch (Exception e) {
            logger.error("Error fetching latest location for deviceID: {} - {}", deviceID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

 // Endpoint to fetch vehicle history based on filters (Vehicle ID, Month, Year)
    @GetMapping("/history")
    public ResponseEntity<List<VehicleHistory>> getVehicleHistory(
        @RequestParam(required = false) String vehicleId,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year) {

        List<VehicleHistory> historyData = vehicleHistoryService.getFilteredVehicleHistory(vehicleId, month, year);

        if (historyData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 if no data
        }

        return ResponseEntity.ok(historyData);
    }

// // Fetch vehicle history with violations based on deviceID in the path
//    @GetMapping("/{deviceID}")
//    public ResponseEntity<List<VehicleHistory>> getVehicleHistoryWithViolations(
//            @PathVariable String deviceID) {
//        List<VehicleHistory> history = vehicleHistoryService.getVehicleHistoryWithViolations(deviceID);
//        return ResponseEntity.ok(history);
//    }

*/
/*
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

// Import SLF4J Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api")
public class VehicleHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleHistoryController.class);

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @PostMapping("/log")
    public ResponseEntity<String> logVehicleData(@RequestParam String deviceID, @RequestBody VehicleHistory historyData) {
        logger.info("Logging vehicle data for deviceID: {}", deviceID);
        vehicleHistoryService.saveVehicleHistory(historyData);
        return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);
    }

    @GetMapping("/history/{deviceID}")
    public ResponseEntity<List<VehicleHistory>> getVehicleHistory(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String toDate
    ) {
        logger.info("Fetching history for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime endDate = LocalDateTime.parse(toDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            List<VehicleHistory> historyData = vehicleHistoryService.getVehicleHistory(deviceID, startTimestamp, endTimestamp);
            if (historyData.isEmpty()) {
                logger.warn("No data found for deviceID: {}", deviceID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            logger.info("Returning {} records for deviceID: {}", historyData.size(), deviceID);
            return ResponseEntity.ok(historyData);
        } catch (Exception e) {
            logger.error("Error fetching vehicle history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint to fetch total distance for a given device and date range
    @GetMapping("/distance/{deviceID}")
    public ResponseEntity<Double> getTotalDistance(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String toDate
    ) {
        logger.info("Calculating total distance for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime endDate = LocalDateTime.parse(toDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            double totalDistance = vehicleHistoryService.calculateTotalDistance(deviceID, startTimestamp, endTimestamp);
            logger.info("Total distance for deviceID: {} is {} km", deviceID, totalDistance);
            return ResponseEntity.ok(totalDistance);
        } catch (Exception e) {
            logger.error("Error calculating total distance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}

*/