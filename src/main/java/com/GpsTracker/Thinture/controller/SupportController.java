package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.SupportTicket;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.service.SupportTicketService;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "*")
public class SupportController {

	
	private static final Logger logger = LoggerFactory.getLogger(SupportController.class);

    @Autowired
    private SupportTicketService supportService;
    

    
    @Autowired
    private UserTypeFilterService userTypeFilterService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/fetch-user-meta")
    public ResponseEntity<Map<String, Object>> fetchUserMeta() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        logger.info("📩 [START] Fetching user meta for email: {}", email);

        UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) {
            logger.warn("❌ No user info found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String userName = "N/A";
        Object userObj = userInfo.userObject();
        if (userObj instanceof SuperAdmin sa) userName = sa.getName();
        else if (userObj instanceof Admin admin) userName = admin.getCompanyName();
        else if (userObj instanceof Dealer dealer) userName = dealer.getCompanyName();
        else if (userObj instanceof Client client) userName = client.getCompanyName();
        else if (userObj instanceof User user) userName = user.getCompanyName();

        logger.info("✅ User identified: Name='{}', Role='{}', ID={}", userName, userInfo.actualUserType(), userInfo.userId());

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("userEmail", email);

        String creator = userTypeFilterService.getCreatorName(userInfo.userId(), userInfo.actualUserType());
        logger.info("🏷️ Creator company/dealer name: {}", creator);
        result.put("dealerClientName", creator);

        try {
            List<String> vehicleNumbers = vehicleRepository.findVehicleNumbersByUserId(userInfo.userId());
            logger.info("🚗 Vehicle numbers for user ID {}: {}", userInfo.userId(), vehicleNumbers);
            result.put("vehicleNumbers", vehicleNumbers);
        } catch (Exception ex) {
            logger.error("🔥 Error fetching vehicle numbers for user ID {}: {}", userInfo.userId(), ex.getMessage());
            result.put("vehicleNumbers", List.of());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 🔎 Get IMEI number by vehicle number
     */
    @GetMapping("/imei-by-number/{vehicleNumber}")
    public ResponseEntity<String> getImeiByVehicleNumber(@PathVariable String vehicleNumber) {
    	
    	
        logger.info("🔍 IMEI fetch requested for vehicle number: {}", vehicleNumber);

        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVehicleNumber(vehicleNumber);

        if (vehicleOpt.isPresent()) {
            String imei = vehicleOpt.get().getImei();
            logger.info("✅ IMEI found for {}: {}", vehicleNumber, imei);
            return ResponseEntity.ok(imei);
        } else {
            logger.warn("❌ No IMEI found for vehicle: {}", vehicleNumber);
            return ResponseEntity.notFound().build();
        }
    }








    /**
     * Submit complaint without file upload
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitTicket(@RequestBody SupportTicket ticket) {
        supportService.submitTicket(ticket);
        return ResponseEntity.ok("✅ Ticket submitted successfully");
    }

    /**
     * Submit complaint with snapshot (multipart)
     */
    @PostMapping(value = "/submit-snapshot", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitTicketWithSnapshot(
            @RequestPart("ticket") SupportTicket ticket,
            @RequestPart(value = "snapshot", required = false) MultipartFile snapshot) {

        try {
            if (snapshot != null && !snapshot.isEmpty()) {
                String uploadPath = new ClassPathResource("static/uploads/snapshots/").getFile().getAbsolutePath();
                File dir = new File(uploadPath);
                if (!dir.exists()) dir.mkdirs();

                String filename = UUID.randomUUID() + "_" + snapshot.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, filename);
                Files.copy(snapshot.getInputStream(), filePath);

                ticket.setSnapshotUrl("/uploads/snapshots/" + filename);
            }

            supportService.submitTicket(ticket);
            return ResponseEntity.ok("✅ Ticket submitted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error submitting ticket: " + e.getMessage());
        }
    }

    /**
     * Get all complaints
     */
    @GetMapping("/all")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(supportService.getAllTickets());
    }

    /**
     * Get tickets raised by current user
     */
    @GetMapping("/my-tickets")
    public ResponseEntity<List<SupportTicket>> getMyTickets() {
        return ResponseEntity.ok(supportService.getMyTickets());
    }

    /**
     * Resolve complaint with response
     */
    @PutMapping("/resolve/{id}")
    public ResponseEntity<String> resolveTicket(@PathVariable Long id, @RequestBody String response) {
        supportService.resolveTicket(id, response);
        return ResponseEntity.ok("✅ Ticket resolved successfully");
    }

    /**
     * Update escalation level and status
     */
    @PutMapping("/escalate/{id}")
    public ResponseEntity<SupportTicket> escalateTicket(
        @PathVariable Long id,
        @RequestParam String status,
        @RequestParam String escalationLevel
    ) {
        SupportTicket updated = supportService.updateEscalation(id, status, escalationLevel);
        return ResponseEntity.ok(updated);
    }

    /**
     * (Optional) Delete ticket (only if allowed by role/policy)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteComplaint(@PathVariable Long id) {
        supportService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint deleted successfully.");
    }

    @PutMapping("/update-escalation/{id}")
    public ResponseEntity<String> updateEscalation(
            @PathVariable Long id,
            @RequestBody Map<String, String> updateFields) {
        String status = updateFields.get("status");
        String escalationLevel = updateFields.get("escalationLevel");
        supportService.updateEscalation(id, status, escalationLevel);
        return ResponseEntity.ok("✅ Complaint updated successfully");
    }

    
    // 🚨 Fetch unread alerts for current user
 // 🚨 Fetch unread alerts for current user
    @GetMapping("/alerts/unread/{role}")
    public ResponseEntity<List<SupportTicket>> getUnreadAlertsByRole(@PathVariable String role) {
        List<SupportTicket> tickets = supportService.getMyTickets();

        List<SupportTicket> unread = tickets.stream()
            .filter(ticket -> !ticket.isRead() && role.equalsIgnoreCase(ticket.getEscalationLevel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(unread);
    }



    // ✅ Mark a support ticket alert as read
    @PutMapping("/alerts/mark-read/{id}")
    public ResponseEntity<String> markAlertAsRead(@PathVariable Long id) {
        supportService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    
}
