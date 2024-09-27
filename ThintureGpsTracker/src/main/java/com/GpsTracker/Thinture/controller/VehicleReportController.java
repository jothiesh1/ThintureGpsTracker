package com.GpsTracker.Thinture.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.dto.VehicleReportDTO;
import com.GpsTracker.Thinture.service.VehicleReportService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/vehicle/report")
public class VehicleReportController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleReportController.class);

    @Autowired
    private VehicleReportService vehicleReportService;

    @GetMapping
    public ResponseEntity<List<VehicleReportDTO>> getVehicleReport(
            @RequestParam("deviceID") String deviceID,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        logger.info("Received request for DeviceID: {}, Month: {}, Year: {}", deviceID, month, year);

        // Construct the timestamp range for the selected month and year
        Timestamp startDate = Timestamp.valueOf(LocalDate.of(year, month, 1).atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(LocalDate.of(year, month, Month.of(month).length(Year.isLeap(year))).atTime(23, 59, 59));

        logger.debug("StartDate: {}, EndDate: {}", startDate, endDate);
        
        List<VehicleReportDTO> report = vehicleReportService.getVehicleReport(deviceID, startDate, endDate);

        if (report.isEmpty()) {
            logger.warn("No content found for DeviceID: {}", deviceID);
            return ResponseEntity.noContent().build();
        }

        logger.info("Returning {} records for DeviceID: {}", report.size(), deviceID);
        return ResponseEntity.ok(report);
    }
}
