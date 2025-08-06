package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.DriverRagReportDTO;
import com.GpsTracker.Thinture.dto.DriverRagSummaryDTO;
import com.GpsTracker.Thinture.repository.DriverRagReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverRagReportService {

    private static final Logger logger = LoggerFactory.getLogger(DriverRagReportService.class);
    private final DriverRagReportRepository repository;

    // RAG scoring constants
    private static final double SPEED_WEIGHT = 0.3;
    private static final double ACCELERATION_WEIGHT = 0.25;
    private static final double BRAKING_WEIGHT = 0.25;
    private static final double TURNING_WEIGHT = 0.2;

    public DriverRagReportService(DriverRagReportRepository repository) {
        this.repository = repository;
    }

    /**
     * Get Driver RAG Report by Driver ID (REAL DATABASE)
     */
    public DriverRagSummaryDTO getDriverRagReportByDriverId(Timestamp start, Timestamp end, Long driverId) {
        logger.info("🔍 Fetching Driver RAG Report for Driver ID: {}, From: {}, To: {}", driverId, start, end);

        List<Object[]> rawData = repository.getDriverRagReportByDriverId(start, end, driverId);
        Object[] performanceSummary = repository.getPerformanceSummaryByDriverId(start, end, driverId);
        
        logger.info("📦 Raw records fetched from DB: {}", rawData.size());

        if (rawData.isEmpty()) {
            logger.warn("⚠️ No data found for Driver ID: {}", driverId);
            return new DriverRagSummaryDTO();
        }

        return processRawDataToSummary(rawData, performanceSummary);
    }

    /**
     * Get Driver RAG Report for a specific device (REAL DATABASE)
     */
    public DriverRagSummaryDTO getDriverRagReport(Timestamp start, Timestamp end, String deviceId) {
        logger.info("🔍 Fetching Driver RAG Report for Device ID: {}, From: {}, To: {}", deviceId, start, end);

        List<Object[]> rawData = repository.getDriverRagReportRaw(start, end, deviceId);
        Object[] performanceSummary = repository.getPerformanceSummary(start, end, deviceId);
        
        logger.info("📦 Raw records fetched from DB: {}", rawData.size());

        if (rawData.isEmpty()) {
            logger.warn("⚠️ No data found for Device ID: {}", deviceId);
            return new DriverRagSummaryDTO();
        }

        return processRawDataToSummary(rawData, performanceSummary);
    }

    /**
     * Get Driver RAG Report for all drivers/devices (REAL DATABASE)
     */
    public DriverRagSummaryDTO getDriverRagReportForAll(Timestamp start, Timestamp end) {
        logger.info("🔍 Fetching Driver RAG Report for ALL drivers, From: {}, To: {}", start, end);
        
        List<Object[]> rawData = repository.getAllDriverRagReportRaw(start, end);
        logger.info("📦 Raw records fetched from DB for ALL: {}", rawData.size());

        if (rawData.isEmpty()) {
            logger.warn("⚠️ No data found for the specified date range");
            return new DriverRagSummaryDTO();
        }

        return processRawDataToSummary(rawData, null);
    }

    /**
     * Process raw data into summary DTO (HANDLES REAL DATA)
     */
    private DriverRagSummaryDTO processRawDataToSummary(List<Object[]> rawData, Object[] performanceSummary) {
        logger.info("🔄 Processing {} raw records into summary", rawData.size());
        
        // Group data by driver (driver_id + driver_name combination)
        Map<String, List<Object[]>> groupedByDriver = rawData.stream()
            .collect(Collectors.groupingBy(row -> {
                Long driverId = row[12] != null ? ((Number) row[12]).longValue() : null;
                String driverName = (String) row[13];
                return (driverId != null ? driverId.toString() : "unassigned") + ":" + 
                       (driverName != null ? driverName : "Unassigned");
            }));

        logger.info("👥 Grouped data by {} unique drivers", groupedByDriver.size());

        List<DriverRagReportDTO> reportList = new ArrayList<>();
        double globalMaxSpeed = 0.0;
        double globalTotalDistance = 0.0;

        for (Map.Entry<String, List<Object[]>> entry : groupedByDriver.entrySet()) {
            String driverKey = entry.getKey();
            List<Object[]> driverData = entry.getValue();
            
            logger.info("🔄 Processing driver: {} with {} records", driverKey, driverData.size());
            
            DriverRagReportDTO reportDTO = processDriverData(driverKey, driverData, performanceSummary);
            if (reportDTO != null) {
                reportList.add(reportDTO);
                globalMaxSpeed = Math.max(globalMaxSpeed, reportDTO.getMaxSpeed());
                globalTotalDistance += reportDTO.getTotalDistance();
                
                logger.info("✅ Added driver: {} - RAG Score: {}", 
                           reportDTO.getDriverName(), reportDTO.getTotalScore());
            }
        }

        DriverRagSummaryDTO summary = new DriverRagSummaryDTO();
        summary.setReportList(reportList);
        summary.setMaxSpeed(globalMaxSpeed);
        summary.setTotalDistance(globalTotalDistance);

        logger.info("✅ Driver RAG Report generated. Records: {}, Max Speed: {}, Total Distance: {}", 
                   reportList.size(), globalMaxSpeed, globalTotalDistance);
        return summary;
    }

    /**
     * Process data for a single driver and calculate RAG metrics (REAL DATA)
     */
    private DriverRagReportDTO processDriverData(String driverKey, List<Object[]> driverData, Object[] performanceSummary) {
        if (driverData.isEmpty()) {
            logger.warn("⚠️ No data for driver: {}", driverKey);
            return null;
        }

        Object[] firstRecord = driverData.get(0);
        
        // Extract basic info from first record (CORRECTED FIELD POSITIONS)
        String deviceId = (String) firstRecord[0];           // device_id
        String vehicleNumber = (String) firstRecord[1];      // vehicle_number
        String vehicleType = (String) firstRecord[2];        // vehicle_type
        String dealerName = (String) firstRecord[3];         // dealer_name
        String ownerName = (String) firstRecord[4];          // owner_name
        String vehicleSerial = (String) firstRecord[5];      // vehicle_serial (not rfid)
        Timestamp timestamp = (Timestamp) firstRecord[6];    // timestamp
        
        // Driver information
        Long driverId = firstRecord[12] != null ? ((Number) firstRecord[12]).longValue() : null;
        String driverName = (String) firstRecord[13];        // driver_name
        String driverEmail = (String) firstRecord[14];       // driver_email
        String driverRfid = (String) firstRecord[15];        // driver_rfid (only from driver table)

        logger.info("👤 Processing driver: ID={}, Name={}, Vehicle={}, Device={}", 
                   driverId, driverName, vehicleNumber, deviceId);

        // Calculate performance metrics from actual data
        PerformanceMetrics metrics = calculatePerformanceMetrics(driverData, performanceSummary);
        
        // Calculate RAG score based on real violations
        double ragScore = calculateRagScore(metrics);

        // Get latest position from actual data
        Double latitude = firstRecord[7] != null ? ((Number) firstRecord[7]).doubleValue() : null;
        Double longitude = firstRecord[8] != null ? ((Number) firstRecord[8]).doubleValue() : null;
        Double speed = firstRecord[9] != null ? ((Number) firstRecord[9]).doubleValue() : null;
        String status = (String) firstRecord[10];
        String additionalData = (String) firstRecord[11];

        // Create DTO with real data
        DriverRagReportDTO dto = new DriverRagReportDTO(
            deviceId, vehicleNumber, vehicleType, dealerName, ownerName, vehicleSerial,
            timestamp, latitude, longitude, speed, status, additionalData,
            driverId, driverName, driverEmail, driverRfid,
            ragScore, metrics.maxSpeed, metrics.totalDistance
        );

        // Set ALL violation metrics from real data
        dto.setSpeedingViolations(metrics.speedingViolations);
        dto.setHarshAccelerationCount(metrics.harshAccelerationCount);
        dto.setHarshBrakingCount(metrics.harshBrakingCount);
        dto.setSharpTurnCount(metrics.sharpTurnCount);
        dto.setViolationCount(metrics.getTotalViolations());

        logger.info("📊 Driver {}: RAG Score={}, Violations=Speed:{}, Accel:{}, Brake:{}, Turn:{}, Total:{}", 
                    driverName, ragScore, 
                    metrics.speedingViolations, metrics.harshAccelerationCount, 
                    metrics.harshBrakingCount, metrics.sharpTurnCount, metrics.getTotalViolations());

        return dto;
    }

    /**
     * Calculate performance metrics from real database data
     */
    private PerformanceMetrics calculatePerformanceMetrics(List<Object[]> data, Object[] performanceSummary) {
        logger.info("📈 Calculating performance metrics for {} records", data.size());
        
        PerformanceMetrics metrics = new PerformanceMetrics();
        
        // Use performance summary from database if available
        if (performanceSummary != null && performanceSummary.length >= 7) {
            logger.info("📊 Using performance summary from database");
            
            metrics.totalRecords = performanceSummary[0] != null ? ((Number) performanceSummary[0]).intValue() : 0;
            metrics.maxSpeed = performanceSummary[1] != null ? ((Number) performanceSummary[1]).doubleValue() : 0.0;
            metrics.avgSpeed = performanceSummary[2] != null ? ((Number) performanceSummary[2]).doubleValue() : 0.0;
            metrics.speedingViolations = performanceSummary[3] != null ? ((Number) performanceSummary[3]).intValue() : 0;
            metrics.harshAccelerationCount = performanceSummary[4] != null ? ((Number) performanceSummary[4]).intValue() : 0;
            metrics.harshBrakingCount = performanceSummary[5] != null ? ((Number) performanceSummary[5]).intValue() : 0;
            metrics.sharpTurnCount = performanceSummary[6] != null ? ((Number) performanceSummary[6]).intValue() : 0;
            
            logger.info("📊 Summary Metrics: Records={}, MaxSpeed={}, Violations={}+{}+{}+{}", 
                       metrics.totalRecords, metrics.maxSpeed,
                       metrics.speedingViolations, metrics.harshAccelerationCount,
                       metrics.harshBrakingCount, metrics.sharpTurnCount);
        } else {
            logger.info("📊 Performance summary not available, calculating from raw data");
            
            // Calculate from raw data if summary not available
            metrics.totalRecords = data.size();
            
            for (Object[] row : data) {
                Double speed = row[9] != null ? ((Number) row[9]).doubleValue() : null;
                String additionalData = (String) row[11];
                
                if (speed != null && speed > metrics.maxSpeed) {
                    metrics.maxSpeed = speed;
                }
                
                // Count violations from additionalData
                if (additionalData != null) {
                    String additionalDataLower = additionalData.toLowerCase();
                    if (additionalDataLower.contains("over speed")) {
                        metrics.speedingViolations++;
                    }
                    if (additionalDataLower.contains("harsh acceleration")) {
                        metrics.harshAccelerationCount++;
                    }
                    if (additionalDataLower.contains("harsh breaking") || additionalDataLower.contains("harsh braking")) {
                        metrics.harshBrakingCount++;
                    }
                    if (additionalDataLower.contains("sharp turning")) {
                        metrics.sharpTurnCount++;
                    }
                }
            }
            
            logger.info("📊 Calculated Metrics: Records={}, MaxSpeed={}, Violations={}+{}+{}+{}", 
                       metrics.totalRecords, metrics.maxSpeed,
                       metrics.speedingViolations, metrics.harshAccelerationCount,
                       metrics.harshBrakingCount, metrics.sharpTurnCount);
        }

        // Calculate distance from GPS points using real coordinates
        metrics.totalDistance = calculateTotalDistance(data);
        logger.info("📏 Total distance calculated: {} km", metrics.totalDistance);
        
        return metrics;
    }

    /**
     * Calculate total distance using Haversine formula from real GPS data
     */
    private double calculateTotalDistance(List<Object[]> data) {
        double totalDistance = 0.0;
        Double lastLat = null, lastLng = null;
        int validPoints = 0;

        for (Object[] row : data) {
            Double latitude = row[7] != null ? ((Number) row[7]).doubleValue() : null;
            Double longitude = row[8] != null ? ((Number) row[8]).doubleValue() : null;

            if (latitude != null && longitude != null && lastLat != null && lastLng != null) {
                double distance = haversine(lastLat, lastLng, latitude, longitude);
                if (distance > 0 && distance < 1000) { // Ignore unrealistic distances
                    totalDistance += distance;
                    validPoints++;
                }
            }
            lastLat = latitude;
            lastLng = longitude;
        }
        
        logger.info("📏 Distance calculation: {} valid GPS points, total distance: {} km", validPoints, totalDistance);
        return totalDistance;
    }

    /**
     * Calculate RAG score based on real performance metrics
     */
    private double calculateRagScore(PerformanceMetrics metrics) {
        double ragScore;
        
        if (metrics.totalDistance <= 0) {
            // If no distance, base score on violations per record
            ragScore = (metrics.speedingViolations * 1.0 + 
                       metrics.harshAccelerationCount * 0.8 + 
                       metrics.harshBrakingCount * 0.8 + 
                       metrics.sharpTurnCount * 0.6) / Math.max(1, metrics.totalRecords) * 10;
            logger.info("📊 RAG Score (no distance): {}", ragScore);
        } else {
            // Normalize violations per 100km
            double speedScore = (metrics.speedingViolations / metrics.totalDistance) * 100 * SPEED_WEIGHT;
            double accelScore = (metrics.harshAccelerationCount / metrics.totalDistance) * 100 * ACCELERATION_WEIGHT;
            double brakeScore = (metrics.harshBrakingCount / metrics.totalDistance) * 100 * BRAKING_WEIGHT;
            double turnScore = (metrics.sharpTurnCount / metrics.totalDistance) * 100 * TURNING_WEIGHT;

            ragScore = speedScore + accelScore + brakeScore + turnScore;
            logger.info("📊 RAG Score (per 100km): Speed={}, Accel={}, Brake={}, Turn={}, Total={}", 
                       speedScore, accelScore, brakeScore, turnScore, ragScore);
        }
        
        return ragScore;
    }

    /**
     * Haversine formula for distance calculation
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Get all device IDs (REAL DATABASE)
     */
    public List<String> getAllDeviceIds() {
        logger.info("🔍 Fetching all device IDs");
        List<String> deviceIds = repository.findAllDeviceIds();
        logger.info("📋 Found {} device IDs", deviceIds.size());
        return deviceIds;
    }

    /**
     * Get all drivers with their devices (REAL DATABASE - MAIN METHOD FOR DROPDOWN)
     */
    public List<Map<String, Object>> getAllDriversWithDevices() {
        logger.info("🔍 SERVICE: getAllDriversWithDevices() called");
        
        try {
            // Call repository
            List<Object[]> rawData = repository.findAllDriversWithDevices();
            logger.info("📦 SERVICE: Repository returned {} raw records", rawData.size());
            
            // Log each raw record for debugging
            for (int i = 0; i < rawData.size(); i++) {
                Object[] row = rawData.get(i);
                logger.info("📝 Raw Record {}: ID={}, Name={}, Email={}, RFID={}, DeviceID={}, VehicleNumber={}, VehicleType={}", 
                           i, row[0], row[1], row[2], row[3], row[4], row[5], row[6]);
            }
            
            // Process each record
            List<Map<String, Object>> result = rawData.stream().map(row -> {
                Map<String, Object> driverDevice = new HashMap<>();
                
                // Extract data with null safety
                Long driverId = row[0] != null ? ((Number) row[0]).longValue() : null;
                String driverName = (String) row[1];
                String driverEmail = (String) row[2];
                String driverRfid = (String) row[3];
                String deviceId = (String) row[4];
                String vehicleNumber = (String) row[5];
                String vehicleType = (String) row[6];
                
                // Log what we're processing
                logger.info("🔄 Processing driver: ID={}, Name='{}', DeviceID='{}', VehicleNumber='{}'", 
                           driverId, driverName, deviceId, vehicleNumber);
                
                // Build map
                driverDevice.put("driverId", driverId);
                driverDevice.put("driverName", driverName);
                driverDevice.put("driverEmail", driverEmail);
                driverDevice.put("driverRfid", driverRfid);
                driverDevice.put("deviceId", deviceId);
                driverDevice.put("vehicleNumber", vehicleNumber);
                driverDevice.put("vehicleType", vehicleType);
                
                logger.info("✅ Created driver map for: {}", driverName);
                return driverDevice;
            }).collect(Collectors.toList());
            
            logger.info("🎯 SERVICE: Returning {} processed driver records", result.size());
            
            // Log final result summary
            result.forEach(driver -> {
                logger.info("👤 Final Driver: ID={}, Name='{}', Vehicle='{}'", 
                           driver.get("driverId"), driver.get("driverName"), driver.get("vehicleNumber"));
            });
            
            return result;
            
        } catch (Exception e) {
            logger.error("❌ SERVICE: Error in getAllDriversWithDevices(): ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get device IDs with driver information (REAL DATABASE)
     */
    public List<Map<String, Object>> getAllDeviceIdsWithDrivers() {
        logger.info("🔍 Fetching all device IDs with driver information");
        
        List<Object[]> rawData = repository.findAllDeviceIdsWithDrivers();
        logger.info("📦 Raw device-driver records: {}", rawData.size());
        
        return rawData.stream().map(row -> {
            Map<String, Object> deviceDriver = new HashMap<>();
            deviceDriver.put("deviceId", row[0]);                    // deviceID
            deviceDriver.put("driverName", row[1] != null ? row[1] : "Unassigned"); // driver_name
            deviceDriver.put("driverEmail", row[2]);                 // driver_email
            deviceDriver.put("vehicleNumber", row[3]);               // vehicleNumber
            deviceDriver.put("vehicleType", row[4]);                 // vehicleType
            
            logger.info("📱 Device mapping: Device={}, Driver={}, Vehicle={}", 
                       row[0], row[1], row[3]);
            
            return deviceDriver;
        }).collect(Collectors.toList());
    }

    /**
     * Inner class for performance metrics
     */
    private static class PerformanceMetrics {
        int totalRecords = 0;
        double maxSpeed = 0.0;
        double avgSpeed = 0.0;
        double totalDistance = 0.0;
        int speedingViolations = 0;
        int harshAccelerationCount = 0;
        int harshBrakingCount = 0;
        int sharpTurnCount = 0;

        int getTotalViolations() {
            return speedingViolations + harshAccelerationCount + harshBrakingCount + sharpTurnCount;
        }
    }
}