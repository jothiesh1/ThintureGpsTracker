package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.DriverRagReportDTO;
import com.GpsTracker.Thinture.service.DriverRagReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.GpsTracker.Thinture.dto.DriverRagSummaryDTO;
import com.GpsTracker.Thinture.service.DriverRagReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class DriverRagReportController {

    private static final Logger logger = LoggerFactory.getLogger(DriverRagReportController.class);
    private final DriverRagReportService service;

    public DriverRagReportController(DriverRagReportService service) {
        this.service = service;
    }

    @GetMapping("/driver-rag")
    public ResponseEntity<DriverRagSummaryDTO> getReport(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(value = "deviceId", required = false) String deviceId) {

        logger.info("📥 Received request for Driver RAG Report");

        Timestamp startTimestamp = Timestamp.valueOf(start);
        Timestamp endTimestamp = Timestamp.valueOf(end);

        DriverRagSummaryDTO summary;
        if (deviceId == null || deviceId.isBlank()) {
            logger.warn("⚠️ No Device ID provided. Fetching report for all devices.");
            summary = service.getDriverRagReportForAll(startTimestamp, endTimestamp); // Create this method
        } else {
            logger.debug("🔍 Parameters: Start = {}, End = {}, Device ID = {}", start, end, deviceId);
            summary = service.getDriverRagReport(startTimestamp, endTimestamp, deviceId);
        }

        logger.info("✅ Summary: Max Speed = {}, Total Distance = {}, Records = {}",
                summary.getMaxSpeed(), summary.getTotalDistance(), summary.getReportList().size());

        return ResponseEntity.ok(summary);
    }



    @GetMapping("/device-ids")
    public ResponseEntity<List<String>> getAllDeviceIds() {
        return ResponseEntity.ok(service.getAllDeviceIds());
    }

}
