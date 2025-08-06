package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.service.VehicleService;

import java.util.HashMap;
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
    
    @Autowired private     VehicleService vehicleSerive;
    


    @Autowired private VehicleService vehicleService;
    @Autowired
    private SupportTicketRepository supportTicketRepo;
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
    
    
    @GetMapping("/complaint-status-count")
    public Map<String, Long> getComplaintStatusCount() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Open", supportTicketRepo.countByStatus("Open"));
        counts.put("In Progress", supportTicketRepo.countByStatus("In Progress"));
        counts.put("Closed", supportTicketRepo.countByStatus("Closed"));
        counts.put("Rejected", supportTicketRepo.countByStatus("Rejected"));
        counts.put("Escalate to Dealer", supportTicketRepo.countByStatus("Escalate to Dealer"));
        counts.put("Escalate to Admin", supportTicketRepo.countByStatus("Escalate to Admin"));
        counts.put("Escalate to Client", supportTicketRepo.countByStatus("Escalate to Client"));
        counts.put("Escalate to SuperAdmin", supportTicketRepo.countByStatus("Escalate to SuperAdmin"));
        return counts;
    }

    

@GetMapping("/vehicle-type-count")
public Map<String, Long> getVehicleTypeCount() {
    logger.info("📊 Fetching vehicle type count from VehicleRepository...");

    List<Object[]> result = vehicleRepository.countByVehicleType();
    Map<String, Long> counts = new LinkedHashMap<>();

    for (Object[] row : result) {
        String type = (String) row[0];
        Long count = ((Number) row[1]).longValue();
        logger.debug("✅ VehicleType: {} | Count: {}", type, count);
        counts.put(type, count);
    }

    logger.info("✅ Total vehicle types counted: {}", counts.size());
    return counts;
}
}
