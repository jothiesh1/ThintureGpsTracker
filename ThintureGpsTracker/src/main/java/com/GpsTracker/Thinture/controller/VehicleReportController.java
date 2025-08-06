package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.GpsTracker.Thinture.dto.VehicleReportDTO;
import com.GpsTracker.Thinture.service.VehicleReportService;
import com.GpsTracker.Thinture.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle/report")
public class VehicleReportController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleReportController.class);

    private static final String PDF_FILE_PATH = "Vehicle_Report.pdf";
    private static final String EXCEL_FILE_PATH = System.getProperty("java.io.tmpdir") + "/Vehicle_Report.xlsx";

    @Autowired
    private VehicleReportService vehicleReportService;

    @Autowired
    private VehicleService vehicleService;

    /**
     * Fetches vehicle reports based on filters.
     */
    @GetMapping
    public ResponseEntity<List<VehicleReportDTO>> getVehicleReports(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String vehicleStatus) {
        logger.info("Fetching vehicle reports: startDate={}, endDate={}, deviceId={}, vehicleStatus={}",
                startDate, endDate, deviceId, vehicleStatus);

        try {
            Timestamp startTimestamp = startDate != null ? parseTimestamp(startDate) : null;
            Timestamp endTimestamp = endDate != null ? parseTimestamp(endDate) : null;

            List<VehicleReportDTO> reports = vehicleReportService.getVehicleReport(startTimestamp, endTimestamp, deviceId, vehicleStatus);

            if (reports.isEmpty()) {
                logger.info("No data found for the given filters.");
                return ResponseEntity.noContent().build();
            }

            logger.info("Returning {} records.", reports.size());
            return ResponseEntity.ok(reports);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error fetching vehicle reports: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Exports vehicle reports to a PDF.
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportReportsToPDF(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String vehicleStatus) {
        logger.info("Exporting vehicle reports to PDF: startDate={}, endDate={}, deviceId={}, vehicleStatus={}",
                startDate, endDate, deviceId, vehicleStatus);

        try {
            Timestamp startTimestamp = startDate != null ? parseTimestamp(startDate) : null;
            Timestamp endTimestamp = endDate != null ? parseTimestamp(endDate) : null;

            List<VehicleReportDTO> reports = vehicleReportService.getVehicleReport(startTimestamp, endTimestamp, deviceId, vehicleStatus);

            if (reports.isEmpty()) {
                logger.info("No data found for the given filters.");
                return ResponseEntity.noContent().build();
            }

            vehicleReportService.exportReportsToPDF(reports, PDF_FILE_PATH);
            logger.info("PDF successfully generated: {}", PDF_FILE_PATH);

            Resource resource = new FileSystemResource(PDF_FILE_PATH);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vehicle_Report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error exporting PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Exports vehicle reports to an Excel file.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportReportsToExcel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String vehicleStatus) {
        logger.info("Exporting vehicle reports to Excel: startDate={}, endDate={}, deviceId={}, vehicleStatus={}",
                startDate, endDate, deviceId, vehicleStatus);

        try {
            Timestamp startTimestamp = startDate != null ? parseTimestamp(startDate) : null;
            Timestamp endTimestamp = endDate != null ? parseTimestamp(endDate) : null;

            List<VehicleReportDTO> reports = vehicleReportService.getVehicleReport(startTimestamp, endTimestamp, deviceId, vehicleStatus);

            if (reports.isEmpty()) {
                logger.info("No data found for the given filters.");
                return ResponseEntity.noContent().build();
            }

            vehicleReportService.exportReportsToExcel(reports, EXCEL_FILE_PATH);
            logger.info("Excel file successfully generated: {}", EXCEL_FILE_PATH);

            Resource resource = new FileSystemResource(EXCEL_FILE_PATH);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vehicle_Report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error exporting Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Fetches all available device IDs.
     */
    @GetMapping("/devices")
    public ResponseEntity<List<String>> getDeviceIDs() {
        try {
            List<String> deviceIDs = vehicleService.getAllDeviceIDs();
            return ResponseEntity.ok(deviceIDs);
        } catch (Exception e) {
            logger.error("Error fetching device IDs: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * Parses a date-time string into a Timestamp object.
     */
    private Timestamp parseTimestamp(String dateTime) {
        try {
            if (dateTime == null || dateTime.isEmpty()) {
                return null;
            }
            if (dateTime.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
                dateTime += ":00"; // Add seconds if missing
            }
            return Timestamp.valueOf(dateTime.replace("T", " "));
        } catch (Exception e) {
            logger.error("Error parsing timestamp: {}", dateTime, e);
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}
