package com.GpsTracker.Thinture.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.GpsTracker.Thinture.dto.VehicleViolationReportDTO;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import com.GpsTracker.Thinture.service.VehicleService;
import com.GpsTracker.Thinture.service.VehicleViolationReportService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicle/violation/report")
public class VehicleViolationReportController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleViolationReportController.class);

    private final VehicleService vehicleService;
    private final VehicleViolationReportService vehicleViolationReportService;

    public VehicleViolationReportController(VehicleService vehicleService,
                                           VehicleViolationReportService vehicleViolationReportService) {
        this.vehicleService = vehicleService;
        this.vehicleViolationReportService = vehicleViolationReportService;
    }

    @GetMapping("/violations/filter")
    public ResponseEntity<List<VehicleViolationReportDTO>> getVehicleViolationsByDateTime(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String additionalData,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String toTime
    ) {
        logger.info("Fetching vehicle violations: fromDate={}, fromTime={}, toDate={}, toTime={}, deviceId={}, additionalData={}",
                fromDate, fromTime, toDate, toTime, deviceId, additionalData);

        try {
            Timestamp startTimestamp = (fromDate != null && fromTime != null)
                    ? parseTimestamp(fromDate + " " + fromTime)
                    : null;

            Timestamp endTimestamp = (toDate != null && toTime != null)
                    ? parseTimestamp(toDate + " " + toTime)
                    : null;

            List<VehicleViolationReportDTO> reports = vehicleViolationReportService.getViolationsByFilters(
                    deviceId,
                    additionalData,
                    startTimestamp,
                    endTimestamp
            );

            return ResponseEntity.ok(reports);

        } catch (Exception e) {
            logger.error("Error fetching reports: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date/time format provided");
        }
    }

    private Timestamp parseTimestamp(String dateTime) throws java.text.ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return new Timestamp(formatter.parse(dateTime).getTime());
    }


   



    @GetMapping("/additional-data")
    public List<String> getAdditionalDataTypes() {
        return Arrays.asList(
            "Speed Crossed",
            "Sharp Turning",
            "Harsh Acceleration",
            "Harsh Breaking",
            "Theft/Towing"
        );
    }

    /**
     * API to fetch all available Device IDs
     */
    @GetMapping("/devices")
    public List<String> getAllDeviceIds() {
        return vehicleService.getAllVehicles().stream()
                .map(vehicle -> vehicle.getDeviceID())
                .collect(Collectors.toList());
    }}
    
    /**
     * Fetch vehicle violations based on Device ID and Additional Data
     */
 