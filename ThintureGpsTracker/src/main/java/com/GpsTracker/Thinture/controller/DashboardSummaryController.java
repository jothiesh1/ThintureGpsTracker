package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.dto.ViolationSummaryDTO;
import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.service.VehicleService;
import com.GpsTracker.Thinture.service.VehicleViolationReportService;

import java.util.Calendar;
import java.util.Date;
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
    
    
    @Autowired
    private VehicleViolationReportService violationReportService;

    @Autowired private VehicleService vehicleService;
    @Autowired
    private SupportTicketRepository supportTicketRepo;
    /**
     * 🚦 API to fetch all role & device counts
     */
    

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



@GetMapping("/violations")
public ResponseEntity<List<ViolationSummaryDTO>> getViolationsSummary() {
    List<ViolationSummaryDTO> summary = violationReportService.getViolationSummary();
    return ResponseEntity.ok(summary);
}





/**
 * Get device expiry report data for dashboard chart
 */
@GetMapping("/device-expiry")
public ResponseEntity<Map<String, Object>> getDeviceExpiryReport() {
    try {
        Map<String, Object> response = new HashMap<>();
        
        // Get expiry counts by date
        List<Object[]> expiryData = vehicleRepository.findDeviceExpiryCountsByDate();
        
        Map<String, Long> expiryCounts = new LinkedHashMap<>();
        for (Object[] row : expiryData) {
            Date expiryDate = (Date) row[0];
            Long count = (Long) row[1];
            
            // Format date as YYYY-MM-DD for frontend
            String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(expiryDate);
            expiryCounts.put(dateStr, count);
        }
        
        // Get summary statistics
        Date currentDate = new Date();
        Long expiredCount = vehicleRepository.countExpiredDevices(currentDate);
        
        // Get upcoming expiries (next 30 days)
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date endDate = cal.getTime();
        
        List<Object[]> upcomingExpiries = vehicleRepository.findExpiringDevicesInDateRange(startDate, endDate);
        
        response.put("expiryCounts", expiryCounts);
        response.put("expiredCount", expiredCount);
        response.put("upcomingExpiryCount", (long) upcomingExpiries.size());
        response.put("upcomingExpiries", formatUpcomingExpiries(upcomingExpiries));
        
        logger.info("Device expiry report generated successfully. Expired: {}, Upcoming: {}", 
                   expiredCount, upcomingExpiries.size());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("Error generating device expiry report", e);
        return ResponseEntity.status(500).body(Map.of("error", "Failed to generate device expiry report"));
    }
}

/**
 * Get detailed device expiry information
 */
@GetMapping("/device-expiry/details")
public ResponseEntity<Map<String, Object>> getDeviceExpiryDetails(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
    
    try {
        Date start = startDate != null ? 
            new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate) : new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date end = endDate != null ? 
            new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate) : cal.getTime();
        
        List<Object[]> devices = vehicleRepository.findExpiringDevicesInDateRange(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("devices", formatDeviceDetails(devices));
        response.put("totalCount", devices.size());
        response.put("dateRange", Map.of("start", start, "end", end));
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("Error getting device expiry details", e);
        return ResponseEntity.status(500).body(Map.of("error", "Failed to get device expiry details"));
    }
}

/**
 * Get renewal status summary
 */
@GetMapping("/renewal-summary")
public ResponseEntity<Map<String, Object>> getRenewalSummary() {
    try {
        Map<String, Long> renewalCounts = vehicleRepository.getRenewalStatusCounts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("renewed", renewalCounts.getOrDefault("renewed", 0L));
        response.put("pending", renewalCounts.getOrDefault("pending", 0L));
        response.put("total", renewalCounts.values().stream().mapToLong(Long::longValue).sum());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("Error getting renewal summary", e);
        return ResponseEntity.status(500).body(Map.of("error", "Failed to get renewal summary"));
    }
}

private List<Map<String, Object>> formatUpcomingExpiries(List<Object[]> expiries) {
    return expiries.stream().map(row -> {
        Map<String, Object> item = new HashMap<>();
        item.put("expiryDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format((Date) row[0]));
        item.put("deviceID", row[1]);
        item.put("vehicleNumber", row[2]);
        item.put("vehicleType", row[3]);
        return item;
    }).collect(java.util.stream.Collectors.toList());
}

private List<Map<String, Object>> formatDeviceDetails(List<Object[]> devices) {
    return devices.stream().map(row -> {
        Map<String, Object> device = new HashMap<>();
        device.put("expiryDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format((Date) row[0]));
        device.put("deviceID", row[1]);
        device.put("vehicleNumber", row[2]);
        device.put("vehicleType", row[3]);
        
        // Calculate days until expiry
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(
            java.time.LocalDate.now(),
            ((Date) row[0]).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        );
        device.put("daysUntilExpiry", daysUntilExpiry);
        device.put("status", daysUntilExpiry < 0 ? "EXPIRED" : daysUntilExpiry <= 7 ? "CRITICAL" : "WARNING");
        
        return device;
    }).collect(java.util.stream.Collectors.toList());
}


/**
 * Get total expired devices count only
 */
@GetMapping("/total-expired")
public ResponseEntity<Map<String, Object>> getTotalExpiredCount() {
    try {
        Date currentDate = new Date();
        Long totalExpired = vehicleRepository.countTotalExpiredDevices(currentDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalExpired", totalExpired);
        
        logger.info("✅ Total expired devices: {}", totalExpired);
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("❌ Error getting total expired count", e);
        return ResponseEntity.status(500).body(Map.of("error", "Failed to get expired count"));
    }
}

// 3. Update your existing getAllCounts() method to include expired count:

@GetMapping("/counts")
public Map<String, Object> getAllCounts() {
    logger.info("📊 Fetching total counts for Admin, Dealer, Client, User, Driver, Device, and Expired");

    Date currentDate = new Date();
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("Admin", adminRepository.count());
    response.put("Dealer", dealerRepository.count());
    response.put("Client", clientRepository.count());
    response.put("User", userRepository.count());
    response.put("Driver", driverRepository.count());
    response.put("Device", vehicleRepository.count());
    response.put("Expired", vehicleRepository.countTotalExpiredDevices(currentDate)); // ✅ Added this

    logger.debug("🔢 Count Summary: {}", response);
    return response;
}
/*
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

*/
}





















