package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.DriverRagSummaryDTO;
import com.GpsTracker.Thinture.service.DriverRagReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class DriverRagReportController {

    private static final Logger logger = LoggerFactory.getLogger(DriverRagReportController.class);
    private final DriverRagReportService driverRagReportService;

    public DriverRagReportController(DriverRagReportService driverRagReportService) {
        this.driverRagReportService = driverRagReportService;
        logger.info("🚀 DriverRagReportController initialized");
    }

    /**
     * Get Driver RAG Performance Report (REAL DATABASE - supports both driverId and deviceId)
     * @param start Start date-time (ISO format: 2025-03-01T00:00:00)
     * @param end End date-time (ISO format: 2025-03-31T23:59:59)
     * @param driverId Optional driver ID filter (NEW)
     * @param deviceId Optional device ID filter (EXISTING - for compatibility)
     * @return Driver RAG Summary with performance metrics from database
     */
    @GetMapping("/driver-rag")
    public ResponseEntity<DriverRagSummaryDTO> getDriverRagReport(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) String deviceId
    ) {
        logger.info("📊 CONTROLLER: Driver RAG Report Request - Start: {}, End: {}, DriverID: {}, DeviceID: {}", 
                   start, end, driverId, deviceId);

        try {
            Timestamp startTimestamp = parseTimestamp(start);
            Timestamp endTimestamp = parseTimestamp(end);
            logger.info("📅 Parsed timestamps - Start: {}, End: {}", startTimestamp, endTimestamp);

            DriverRagSummaryDTO report;
            
            // Priority: driverId > deviceId > all drivers
            if (driverId != null) {
                logger.info("🎯 Querying by Driver ID: {}", driverId);
                report = driverRagReportService.getDriverRagReportByDriverId(startTimestamp, endTimestamp, driverId);
                logger.info("✅ Fetched real report for Driver ID: {} - Records: {}", 
                           driverId, report.getReportList() != null ? report.getReportList().size() : 0);
            } else if (deviceId != null && !deviceId.trim().isEmpty()) {
                logger.info("🎯 Querying by Device ID: {}", deviceId);
                report = driverRagReportService.getDriverRagReport(startTimestamp, endTimestamp, deviceId);
                logger.info("✅ Fetched real report for Device ID: {} - Records: {}", 
                           deviceId, report.getReportList() != null ? report.getReportList().size() : 0);
            } else {
                logger.info("🎯 Querying ALL drivers");
                report = driverRagReportService.getDriverRagReportForAll(startTimestamp, endTimestamp);
                logger.info("✅ Fetched real report for ALL drivers - Records: {}", 
                           report.getReportList() != null ? report.getReportList().size() : 0);
            }

            // Log report summary
            if (report.getReportList() != null && !report.getReportList().isEmpty()) {
                logger.info("📈 Report Summary: MaxSpeed={}, TotalDistance={}, DriverCount={}", 
                           report.getMaxSpeed(), report.getTotalDistance(), report.getReportList().size());
                
                // Log first few drivers
                for (int i = 0; i < Math.min(3, report.getReportList().size()); i++) {
                    var driver = report.getReportList().get(i);
                    logger.info("👤 Driver {}: Name='{}', RAG Score={}, Violations={}", 
                               i+1, driver.getDriverName(), driver.getTotalScore(), driver.getViolationCount());
                }
            } else {
                logger.warn("⚠️ No drivers found in report");
            }

            logger.info("✅ CONTROLLER: Driver RAG Report response prepared successfully");
            return ResponseEntity.ok(report);

        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Error generating Driver RAG Report: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Error generating Driver RAG Report: " + e.getMessage());
        }
    }

    /**
     * Get all Device IDs for dropdown (REAL DATABASE)
     */
    @GetMapping("/device-ids")
    public ResponseEntity<List<String>> getAllDeviceIds() {
        logger.info("📋 CONTROLLER: getAllDeviceIds() called");
        
        try {
            List<String> deviceIds = driverRagReportService.getAllDeviceIds();
            logger.info("✅ CONTROLLER: Retrieved {} device IDs from service", deviceIds.size());
            
            // Log first few device IDs
            for (int i = 0; i < Math.min(5, deviceIds.size()); i++) {
                logger.info("📱 Device {}: {}", i+1, deviceIds.get(i));
            }
            
            return ResponseEntity.ok(deviceIds);
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Error fetching device IDs: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error fetching device IDs: " + e.getMessage());
        }
    }

    /**
     * Get all Drivers for dropdown (REAL DATABASE - MAIN ENDPOINT FOR DRIVER-FOCUSED UI)
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<Map<String, Object>>> getAllDrivers() {
        logger.info("👥 CONTROLLER: getAllDrivers() called - MAIN ENDPOINT FOR DROPDOWN");
        
        try {
            logger.info("🔄 CONTROLLER: Calling service.getAllDriversWithDevices()...");
            List<Map<String, Object>> drivers = driverRagReportService.getAllDriversWithDevices();
            
            logger.info("📦 CONTROLLER: Service returned {} drivers", drivers.size());
            
            // Log each driver for debugging
            for (int i = 0; i < drivers.size(); i++) {
                Map<String, Object> driver = drivers.get(i);
                logger.info("👤 CONTROLLER Driver {}: ID={}, Name='{}', Email='{}', Vehicle='{}', Device='{}'", 
                           i+1, 
                           driver.get("driverId"), 
                           driver.get("driverName"), 
                           driver.get("driverEmail"),
                           driver.get("vehicleNumber"),
                           driver.get("deviceId"));
            }
            
            if (drivers.isEmpty()) {
                logger.warn("⚠️ CONTROLLER: No drivers found! This will cause empty dropdown.");
            } else {
                logger.info("✅ CONTROLLER: Successfully returning {} drivers for dropdown", drivers.size());
            }
            
            return ResponseEntity.ok(drivers);
            
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Error in getAllDrivers(): ", e);
            logger.error("❌ CONTROLLER: Stack trace: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error fetching drivers: " + e.getMessage());
        }
    }

    /**
     * Get all Device IDs with Driver Names for dropdown (REAL DATABASE)
     */
    @GetMapping("/device-ids-with-drivers")
    public ResponseEntity<List<Map<String, Object>>> getAllDeviceIdsWithDrivers() {
        logger.info("📋 CONTROLLER: getAllDeviceIdsWithDrivers() called");
        
        try {
            List<Map<String, Object>> deviceDrivers = driverRagReportService.getAllDeviceIdsWithDrivers();
            logger.info("✅ CONTROLLER: Retrieved {} device-driver mappings", deviceDrivers.size());
            
            // Log first few mappings
            for (int i = 0; i < Math.min(3, deviceDrivers.size()); i++) {
                Map<String, Object> mapping = deviceDrivers.get(i);
                logger.info("📱 Mapping {}: Device='{}', Driver='{}', Vehicle='{}'", 
                           i+1, mapping.get("deviceId"), mapping.get("driverName"), mapping.get("vehicleNumber"));
            }
            
            return ResponseEntity.ok(deviceDrivers);
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Error fetching device-driver mappings: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error fetching device-driver mappings: " + e.getMessage());
        }
    }

    /**
     * Get all Drivers with their Device IDs (REAL DATABASE - ALIAS)
     */
    @GetMapping("/drivers-with-devices")
    public ResponseEntity<List<Map<String, Object>>> getAllDriversWithDevices() {
        logger.info("👥 CONTROLLER: getAllDriversWithDevices() called (alias endpoint)");
        
        try {
            List<Map<String, Object>> driversWithDevices = driverRagReportService.getAllDriversWithDevices();
            logger.info("✅ CONTROLLER: Retrieved {} driver-device mappings", driversWithDevices.size());
            return ResponseEntity.ok(driversWithDevices);
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Error fetching driver-device mappings: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error fetching driver-device mappings: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint for Driver RAG Report service (REAL DATABASE)
     */
    @GetMapping("/driver-rag/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("🏥 CONTROLLER: Health check called");
        
        try {
            List<String> deviceIds = driverRagReportService.getAllDeviceIds();
            List<Map<String, Object>> drivers = driverRagReportService.getAllDriversWithDevices();
            
            Map<String, Object> healthInfo = Map.of(
                "status", "healthy",
                "deviceCount", deviceIds.size(),
                "driverCount", drivers.size(),
                "timestamp", System.currentTimeMillis(),
                "dataSource", "real_database"
            );
            
            logger.info("✅ CONTROLLER: Health check passed - Devices: {}, Drivers: {}", 
                       deviceIds.size(), drivers.size());
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Health check failed: ", e);
            
            Map<String, Object> errorInfo = Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis(),
                "dataSource", "real_database"
            );
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorInfo);
        }
    }

    /**
     * DEBUG: Direct database test endpoint
     */
    @GetMapping("/debug/drivers-test")
    public ResponseEntity<Map<String, Object>> debugDriversTest() {
        logger.info("🔧 CONTROLLER: DEBUG endpoint called");
        
        try {
            logger.info("🔄 CONTROLLER: Testing service call...");
            List<Map<String, Object>> drivers = driverRagReportService.getAllDriversWithDevices();
            
            logger.info("📊 CONTROLLER: DEBUG - Service returned {} drivers", drivers.size());
            
            Map<String, Object> debugInfo = Map.of(
                "status", "success",
                "endpoint", "/api/reports/debug/drivers-test",
                "driversFound", drivers.size(),
                "drivers", drivers,
                "message", "Check server logs for detailed information"
            );
            
            logger.info("✅ CONTROLLER: DEBUG endpoint completed successfully");
            return ResponseEntity.ok(debugInfo);
            
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: DEBUG endpoint failed: ", e);
            
            Map<String, Object> errorInfo = Map.of(
                "status", "error",
                "endpoint", "/api/reports/debug/drivers-test",
                "error", e.getMessage(),
                "message", "Check server logs for error details"
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }

    /**
     * Parse timestamp from ISO string format
     */
    private Timestamp parseTimestamp(String dateTimeStr) throws Exception {
        logger.info("📅 Parsing timestamp: {}", dateTimeStr);
        
        try {
            // Handle ISO format: 2025-03-01T00:00:00
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Timestamp result = new Timestamp(formatter.parse(dateTimeStr).getTime());
            logger.info("✅ Parsed ISO timestamp: {} -> {}", dateTimeStr, result);
            return result;
        } catch (Exception e) {
            logger.warn("⚠️ ISO format failed, trying date-only format");
            // Fallback to date only format: 2025-03-01
            try {
                SimpleDateFormat fallbackFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp result = new Timestamp(fallbackFormatter.parse(dateTimeStr).getTime());
                logger.info("✅ Parsed date-only timestamp: {} -> {}", dateTimeStr, result);
                return result;
            } catch (Exception ex) {
                logger.error("❌ Failed to parse timestamp with both formats: {}", dateTimeStr);
                throw new Exception("Invalid date format. Use ISO format: 2025-03-01T00:00:00 or 2025-03-01");
            }
        }
    }
}