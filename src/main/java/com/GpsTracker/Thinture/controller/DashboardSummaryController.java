package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.service.VehicleService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard-summary")
public class DashboardSummaryController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardSummaryController.class);

    @Autowired private AdminRepository adminRepository;
    @Autowired private DealerRepository dealerRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private VehicleRepository vehicleRepository;

    @Autowired private VehicleService vehicleService;

    /**
     * 🚦 API to fetch all role & device counts
     */
    @GetMapping("/counts")
    public Map<String, Object> getAllCounts() {
        logger.info("📊 Fetching total counts for Admin, Dealer, Client, User, Driver, and Device");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Admin", adminRepository.count());
        response.put("Dealer", dealerRepository.count());
        response.put("Client", clientRepository.count());
        response.put("User", userRepository.count());
        response.put("Driver", driverRepository.count());
        response.put("Device", vehicleRepository.count());

        logger.debug("🔢 Count Summary: {}", response);
        return response;
    }

    /**
     * 📈 API to fetch monthly installation and renewal report
     */
    @GetMapping("/monthly-report")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyInstallationRenewalReport() {
        logger.info("📅 Fetching monthly installation and renewal summary report");
        List<Map<String, Object>> report = vehicleService.getMonthlyInstallationRenewalReport();
        logger.debug("📦 Report Data: {}", report);
        return ResponseEntity.ok(report);
    }
}
