package com.GpsTracker.Thinture.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.dto.ViolationReportDTO;
import com.GpsTracker.Thinture.model.ViolationReport;
import com.GpsTracker.Thinture.service.ViolationReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.List;
@RestController
@RequestMapping("/api/reports")
public class ViolationReportController {

    private static final Logger logger = LoggerFactory.getLogger(ViolationReportController.class);

    @Autowired
    private ViolationReportService violationReportService;

    @GetMapping
    public ResponseEntity<List<ViolationReportDTO>> getViolationReports(
            @RequestParam String month,
            @RequestParam String year,
            @RequestParam String vehicleId) {

        logger.info("Received request for reports with vehicleId: {}, month: {}, year: {}", vehicleId, month, year);

        try {
            List<ViolationReportDTO> reports = violationReportService.getViolationReports(vehicleId, month, year);
            logger.info("Successfully fetched {} reports for vehicleId: {}", reports.size(), vehicleId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error fetching reports for vehicleId: {}", vehicleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
