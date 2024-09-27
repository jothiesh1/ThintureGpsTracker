package com.GpsTracker.Thinture.service;
import com.GpsTracker.Thinture.dto.ViolationReportDTO;
import com.GpsTracker.Thinture.model.GpsData;
//Import SLF4J Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.ViolationReport;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.repository.ViolationReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/*
@Service
public class ViolationReportService {

    private static final Logger logger = LoggerFactory.getLogger(ViolationReportService.class);

    @Autowired
    private ViolationReportRepository violationReportRepository;

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @Autowired
    private VehicleRepository vehicleRepository;
/*
    // Process vehicle history logs and generate violation reports
    public void processVehicleHistoryForViolations(String deviceID, Timestamp startDate, Timestamp endDate) {
        logger.info("Processing violations for deviceID: {}", deviceID);

        // Fetch vehicle history logs within the date range
        List<VehicleHistory> historyLogs = vehicleHistoryService.getHistoryInRange(deviceID, startDate, endDate);

        // Fetch the Vehicle entity based on deviceID
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByDeviceID(deviceID);

        if (!vehicleOpt.isPresent()) {
            logger.error("Vehicle with deviceID: {} not found", deviceID);
            return;  // Exit early if vehicle is not found
        }

        Vehicle vehicle = vehicleOpt.get();  // Extract the actual vehicle from Optional

        // Iterate through the history logs to check for violations
        for (VehicleHistory history : historyLogs) {
            // Example violation condition: Over-speeding
            if (history.getSpeed() > 80) {
                createViolationReport(vehicle, history, "Over Speeding");
            }

            // Add more conditions here for other types of violations (e.g., harsh driving, seatbelt violations)
        }
    }

    // Method to create and save a violation report
    private void createViolationReport(Vehicle vehicle, VehicleHistory history, String violationType) {
        ViolationReport violationReport = new ViolationReport();
        
        // Set the vehicle object directly (it contains the deviceID internally)
        violationReport.setVehicle(vehicle);  
        violationReport.setViolationDate(history.getTimestamp());
        violationReport.setViolationType(violationType);
        violationReport.setSpeed(history.getSpeed());
        violationReport.setLocation(history.getLatitude() + ", " + history.getLongitude());
        violationReport.setStatus("Active");

        try {
            violationReportRepository.save(violationReport);
            logger.info("Created {} violation for deviceID: {} at speed: {}", violationType, vehicle.getDeviceID(), history.getSpeed());
        } catch (Exception e) {
            logger.error("Error creating violation for deviceID: {} - {}", vehicle.getDeviceID(), e.getMessage(), e);
        }
    }

    // Fetch violation reports for a specific deviceID within a date range
    public List<ViolationReport> getViolationReports(String deviceID, Timestamp startDate, Timestamp endDate) {
        logger.info("Fetching violation reports for deviceID: {} from {} to {}", deviceID, startDate, endDate);
        return violationReportRepository.findByVehicle_DeviceIDAndViolationDateBetween(deviceID, startDate, endDate);
    }
    
    

   

    public List<ViolationReport> getViolationsByDeviceId(String deviceId) {
        logger.info("Fetching violations for deviceId: {}", deviceId);
        try {
            List<ViolationReport> violations = violationReportRepository.findByVehicleDeviceID(deviceId);
            logger.info("Successfully fetched {} violations for deviceId: {}", violations.size(), deviceId);
            return violations;
        } catch (Exception e) {
            logger.error("Error fetching violations for deviceId: {}", deviceId, e);
            throw e; // Rethrow exception after logging
        }
    }*/
@Service
public class ViolationReportService {

    private static final Logger logger = LoggerFactory.getLogger(ViolationReportService.class);

    @Autowired
    private ViolationReportRepository violationReportRepository;

    /**
     * Fetches violation reports based on deviceId, month, and year.
     *
     * @param deviceId The ID of the vehicle device
     * @param month    The month for filtering reports
     * @param year     The year for filtering reports
     * @return A list of ViolationReportDTO containing violation details
     */
    public List<ViolationReportDTO> getViolationReports(String deviceId, String month, String year) {
        logger.info("Fetching violation reports for deviceId: {}, month: {}, year: {}", deviceId, month, year);

        try {
            // Convert month and year to start and end timestamps
            Timestamp startDate = Timestamp.valueOf(year + "-" + month + "-01 00:00:00");
            Timestamp endDate = Timestamp.valueOf(year + "-" + month + "-31 23:59:59");

            // Log the query parameters
            logger.debug("Start Date: {}, End Date: {}", startDate, endDate);

            // Fetch reports from repository
            List<ViolationReport> violationReports = violationReportRepository.findByVehicle_DeviceIDAndViolationDateBetween(deviceId, startDate, endDate);

            // If no reports are found, log and return an empty list
            if (violationReports.isEmpty()) {
                logger.info("No violation reports found for deviceId: {} between {} and {}", deviceId, startDate, endDate);
                return new ArrayList<>();
            }

            // Log the number of reports found
            logger.info("Found {} violation reports for deviceId: {}", violationReports.size(), deviceId);

            // Map entities to DTOs
            return violationReports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error while fetching violation reports for deviceId: {}", deviceId, e);
            throw e;  // rethrow exception to the calling layer for handling
        }
    }

    /**
     * Helper method to map ViolationReport entity to ViolationReportDTO
     *
     * @param report The ViolationReport entity
     * @return A ViolationReportDTO
     */
    private ViolationReportDTO convertToDTO(ViolationReport report) {
        ViolationReportDTO dto = new ViolationReportDTO();
        dto.setId(report.getId());
        dto.setDeviceId(report.getVehicle().getDeviceID());
        dto.setViolationDate(report.getViolationDate());
        dto.setViolationType(report.getViolationType());
        dto.setSpeed(report.getSpeed());
        dto.setLocation(report.getLocation());
        dto.setStatus(report.getStatus());

        logger.debug("Converted ViolationReport to ViolationReportDTO for deviceId: {}", report.getVehicle().getDeviceID());
        return dto;
    }
}
