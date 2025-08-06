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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RequestMapping("/api")
//@RequestMapping("/api")
@RestController
@RequestMapping("/api/vehicle")
public class VehicleHistoryController {

  private static final Logger logger = LoggerFactory.getLogger(VehicleHistoryController.class);

  @Autowired
  private VehicleHistoryService vehicleHistoryService;

  // Endpoint to get vehicle details based on deviceID

// Get vehicle details based on deviceID
  @GetMapping("/details/{deviceID}")
  public ResponseEntity<VehicleHistory> getVehicleDetails(@PathVariable("deviceID") String deviceID) {
      logger.info("Fetching vehicle details for Device ID: {}", deviceID);

      VehicleHistory vehicleDetails = vehicleHistoryService.getLatestLocation(deviceID);

      if (vehicleDetails != null) {
          logger.info("Vehicle found: {}", vehicleDetails);
          return ResponseEntity.ok(vehicleDetails);
      } else {
          logger.warn("No vehicle details found for Device ID: {}", deviceID);
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Send 404 if not found
      }
  
}

     
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
  
 


  @GetMapping("/history/{deviceID}")
  public ResponseEntity<?> getVehicleHistory(
          @PathVariable("deviceID") String deviceID,
          @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String fromDate,
          @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String toDate
  ) {
      logger.info("🚀 [START] Fetching history for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);

      try {
          // ✅ Step 1: Log received parameters
          logger.info("🛠️ Received deviceID: {}", deviceID);
          logger.info("📌 Raw fromDate: {}", fromDate);
          logger.info("📌 Raw toDate: {}", toDate);

          // ✅ Step 2: Convert Strings to Timestamp
          Timestamp startTimestamp = Timestamp.valueOf(fromDate.replace("T", " "));
          Timestamp endTimestamp = Timestamp.valueOf(toDate.replace("T", " "));

          logger.info("⏳ Converted startTimestamp: {}", startTimestamp);
          logger.info("⏳ Converted endTimestamp: {}", endTimestamp);

          // ✅ Step 3: Validate date range
          if (startTimestamp.after(endTimestamp)) {
              logger.warn("⚠️ Start date {} is after end date {}. Returning BAD REQUEST.", startTimestamp, endTimestamp);
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("⚠️ Error: Start date must be earlier than end date.");
          }

          // ✅ Step 4: Fetch history from service
          logger.info("🔍 Fetching history from database...");
          List<VehicleHistory> historyData = vehicleHistoryService.getVehicleHistory(deviceID, startTimestamp, endTimestamp);
          logger.info("✅ Fetch operation completed.");

          // ✅ Step 5: Handle empty results
          if (historyData == null || historyData.isEmpty()) {
              logger.warn("⚠️ No history data found for deviceID: {}", deviceID);
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No history found for the given device and date range.");
          }

          // ✅ Step 6: Log total records count
          logger.info("📊 Total records fetched: {}", historyData.size());

          // ✅ Step 7: Return response
          logger.info("✅ [END] Returning {} history records for deviceID: {}", historyData.size(), deviceID);
          return ResponseEntity.ok(historyData);
      } catch (Exception e) {
          logger.error("❌ Exception occurred while fetching history for deviceID: {}: {}", deviceID, e.getMessage(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("An unexpected error occurred while fetching vehicle history.");
      }
  }




  
  @GetMapping("/distance/{deviceID}")
  public ResponseEntity<?> getTotalDistance(
          @PathVariable("deviceID") String deviceID,
          @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String fromDate,
          @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") String toDate
  ) {

      logger.info("Calculating total distance for deviceID: {}, from: {}, to: {}", deviceID, fromDate, toDate);

      try {
          // Parse dates as LocalDateTime (without timezone)
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
          LocalDateTime startDate = LocalDateTime.parse(fromDate, formatter);
          LocalDateTime endDate = LocalDateTime.parse(toDate, formatter);

          if (startDate.isAfter(endDate)) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Start date must be earlier than end date.");
          }

          // Convert LocalDateTime to Timestamp
          Timestamp startTimestamp = Timestamp.valueOf(startDate);
          Timestamp endTimestamp = Timestamp.valueOf(endDate);

          // Calculate total distance
          double totalDistance = vehicleHistoryService.calculateTotalDistance(deviceID, startTimestamp, endTimestamp);

          logger.info("Total distance for deviceID: {} is {} km", deviceID, totalDistance);
          return ResponseEntity.ok(totalDistance);
      } catch (Exception e) {
          logger.error("Error calculating total distance: {}", e.getMessage(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("An unexpected error occurred while calculating total distance.");
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
// Endpoint to save vehicle history
  @PostMapping("/log")
  public ResponseEntity<String> logVehicleData(@RequestParam String deviceID, @RequestBody VehicleHistory historyData) {
      logger.info("Logging vehicle data for deviceID: {}", deviceID);
      vehicleHistoryService.saveVehicleHistory(historyData);
      return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);
  
}


}
/*
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