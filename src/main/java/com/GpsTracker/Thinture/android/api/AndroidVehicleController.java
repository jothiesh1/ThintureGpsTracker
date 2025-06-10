package com.GpsTracker.Thinture.android.api;

import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/android/vehicle")
public class AndroidVehicleController {

    private static final Logger logger = LoggerFactory.getLogger(AndroidVehicleController.class);

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @GetMapping("/details/{deviceID}")
    public ResponseEntity<VehicleHistory> getVehicleDetails(@PathVariable("deviceID") String deviceID) {
        logger.info("📡 Android → Fetching vehicle details for: {}", deviceID);
        VehicleHistory vehicle = vehicleHistoryService.getLatestLocation(deviceID);
        return vehicle != null ?
                ResponseEntity.ok(vehicle) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/latest-location/{deviceID}")
    public ResponseEntity<VehicleHistory> getLatestLocation(@PathVariable("deviceID") String deviceID) {
        logger.info("📍 Android → Fetching latest location for: {}", deviceID);
        VehicleHistory latest = vehicleHistoryService.getLatestLocation(deviceID);
        return latest != null ?
                ResponseEntity.ok(latest) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/history")
    public ResponseEntity<List<VehicleHistory>> getHistoryByMonthYear(
            @RequestParam("deviceID") String deviceID,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        logger.info("📊 Android → History for {}, month: {}, year: {}", deviceID, month, year);
        List<VehicleHistory> history = vehicleHistoryService.findByDeviceIDMonthYear(deviceID, month, year);
        return history.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(history);
    }

    @GetMapping("/history/{deviceID}")
    public ResponseEntity<?> getVehicleHistoryRange(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String to) {

        try {
            logger.info("⏱️ Android → Fetching history for {} from {} to {}", deviceID, from, to);
            Timestamp start = Timestamp.valueOf(from.replace("T", " "));
            Timestamp end = Timestamp.valueOf(to.replace("T", " "));

            if (start.after(end)) {
                return ResponseEntity.badRequest().body("Start date must be before end date.");
            }

            List<VehicleHistory> history = vehicleHistoryService.getVehicleHistory(deviceID, start, end);
            return history.isEmpty() ?
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("No history found") :
                    ResponseEntity.ok(history);

        } catch (Exception e) {
            logger.error("❌ Android → Error fetching history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request.");
        }
    }

    @GetMapping("/distance/{deviceID}")
    public ResponseEntity<?> getTotalDistance(
            @PathVariable("deviceID") String deviceID,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String to) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(from, formatter);
            LocalDateTime end = LocalDateTime.parse(to, formatter);

            if (start.isAfter(end)) {
                return ResponseEntity.badRequest().body("Start date must be before end date.");
            }

            double distance = vehicleHistoryService.calculateTotalDistance(deviceID,
                    Timestamp.valueOf(start), Timestamp.valueOf(end));

            return ResponseEntity.ok(distance);

        } catch (Exception e) {
            logger.error("❌ Android → Error calculating distance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating total distance.");
        }
    }

    @PostMapping("/log")
    public ResponseEntity<String> logVehicleData(@RequestParam String deviceID, @RequestBody VehicleHistory data) {
        logger.info("📥 Android → Logging vehicle data for deviceID: {}", deviceID);
        vehicleHistoryService.saveVehicleHistory(data);
        return ResponseEntity.ok("Data saved successfully");
    }
}
