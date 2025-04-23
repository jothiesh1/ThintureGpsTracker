package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.dto.PanicAlertDTO;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.service.UserService;
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class PanicAlertController {

    private final Logger logger = LoggerFactory.getLogger(PanicAlertController.class);

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    // Get panic alerts for all devices
    @GetMapping("/panic-alerts")
    public ResponseEntity<List<PanicAlertDTO>> getPanicAlerts() {
        logger.info("Fetching all panic alerts");
        try {
            List<PanicAlertDTO> panicAlerts = vehicleHistoryService.getAllPanicAlerts();

            if (panicAlerts.isEmpty()) {
                logger.warn("No panic alerts found.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                logger.info("Panic alerts found: {}", panicAlerts.size());
                return ResponseEntity.ok(panicAlerts);
            }
        } catch (Exception e) {
            logger.error("Error fetching panic alerts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
