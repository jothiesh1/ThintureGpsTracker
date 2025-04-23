package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.SupportTicket;
import com.GpsTracker.Thinture.repository.SupportTicketRepository;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupportTicketService {

    @Autowired
    private SupportTicketRepository supportRepo;

    @Autowired
    private UserTypeFilterService userTypeFilterService;

    private static final String IMAGE_UPLOAD_DIR = "uploads/support_snapshots/";

    public List<SupportTicket> getMyTickets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) throw new RuntimeException("User not found for email: " + email);

        return switch (userInfo.actualUserType()) {
            case "SUPERADMIN" -> supportRepo.findBySuperadminId(userInfo.userId());
            case "ADMIN" -> supportRepo.findByAdminId(userInfo.userId());
            case "DEALER" -> supportRepo.findByDealerId(userInfo.userId());
            case "CLIENT" -> supportRepo.findByClientId(userInfo.userId());
            case "USER" -> supportRepo.findByUserId(userInfo.userId());
            case "DRIVER" -> supportRepo.findByDriverId(userInfo.userId());
            default -> throw new RuntimeException("Unsupported role: " + userInfo.actualUserType());
        };
    }

    public List<SupportTicket> getAllTickets() {
        return supportRepo.findAll();
    }

    public SupportTicket resolveTicket(Long id, String response) {
        SupportTicket ticket = supportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus("Resolved");
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setResponse(response);
        return supportRepo.save(ticket);
    }

    public SupportTicket submitTicket(SupportTicket ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) throw new RuntimeException("User not found");

        ticket.setSubmittedAt(LocalDateTime.now());
        ticket.setStatus("Pending");
        ticket.setRole(userInfo.actualUserType());

        switch (userInfo.actualUserType()) {
            case "SUPERADMIN" -> ticket.setSuperadminId(userInfo.userId());
            case "ADMIN" -> ticket.setAdminId(userInfo.userId());
            case "DEALER" -> ticket.setDealerId(userInfo.userId());
            case "CLIENT" -> ticket.setClientId(userInfo.userId());
            case "USER" -> ticket.setUserId(userInfo.userId());
            case "DRIVER" -> ticket.setDriverId(userInfo.userId());
            default -> throw new RuntimeException("Unsupported role: " + userInfo.actualUserType());
        }

        return supportRepo.save(ticket);
    }

    public String submitTicketWithSnapshot(String ticketJson, MultipartFile snapshot) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) throw new RuntimeException("User not found for email: " + email);

        SupportTicket ticket = new com.fasterxml.jackson.databind.ObjectMapper().readValue(ticketJson, SupportTicket.class);

        ticket.setSubmittedAt(LocalDateTime.now());
        ticket.setStatus("Pending");
        ticket.setRole(userInfo.actualUserType());

        switch (userInfo.actualUserType()) {
            case "SUPERADMIN" -> ticket.setSuperadminId(userInfo.userId());
            case "ADMIN" -> ticket.setAdminId(userInfo.userId());
            case "DEALER" -> ticket.setDealerId(userInfo.userId());
            case "CLIENT" -> ticket.setClientId(userInfo.userId());
            case "USER" -> ticket.setUserId(userInfo.userId());
            case "DRIVER" -> ticket.setDriverId(userInfo.userId());
            default -> throw new RuntimeException("Unsupported role: " + userInfo.actualUserType());
        }

        if (snapshot != null && !snapshot.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + snapshot.getOriginalFilename();
            File uploadDir = new File(IMAGE_UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            File savedFile = new File(uploadDir, filename);
            snapshot.transferTo(savedFile);

            ticket.setSnapshotUrl("/" + IMAGE_UPLOAD_DIR + filename);
        }

        supportRepo.save(ticket);
        return "Ticket submitted successfully!";
    }

    // 🔄 Escalation Update Method
    public SupportTicket updateEscalation(Long id, String status, String escalationLevel) {
        SupportTicket ticket = supportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(status);
        ticket.setEscalationLevel(escalationLevel);
        return supportRepo.save(ticket);
    }
    
    //delete
    public void deleteComplaint(Long id) {
        SupportTicket ticket = supportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
        supportRepo.delete(ticket);
    }

    
    public void markAsRead(Long id) {
        SupportTicket ticket = supportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setRead(true);
        supportRepo.save(ticket);
    }
    
    
    

    public SupportTicket escalateTicket(SupportTicket ticket, String escalateTo, Long escalateToId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UserTypeResult userInfo = userTypeFilterService.findUserAndTypeByEmail(email);
        if (userInfo == null) throw new RuntimeException("User not found for email: " + email);

        ticket.setSubmittedAt(LocalDateTime.now());
        ticket.setStatus("Escalated");
        ticket.setRole(userInfo.actualUserType());
        ticket.setEscalationStatus("Escalated");

        // Who is raising it
        switch (userInfo.actualUserType()) {
            case "SUPERADMIN" -> ticket.setSuperadminId(userInfo.userId());
            case "ADMIN" -> ticket.setAdminId(userInfo.userId());
            case "DEALER" -> ticket.setDealerId(userInfo.userId());
            case "CLIENT" -> ticket.setClientId(userInfo.userId());
            case "USER" -> ticket.setUserId(userInfo.userId());
            case "DRIVER" -> ticket.setDriverId(userInfo.userId());
        }

        // Who is it escalated to
        switch (escalateTo.toUpperCase()) {
            case "ADMIN" -> ticket.setAdminId(escalateToId);
            case "DEALER" -> ticket.setDealerId(escalateToId);
            case "CLIENT" -> ticket.setClientId(escalateToId);
            case "SUPERADMIN" -> ticket.setSuperadminId(escalateToId);
            default -> throw new RuntimeException("Invalid escalation target: " + escalateTo);
        }

        ticket.setEscalationLevel(escalateTo);

        return supportRepo.save(ticket);
    }


} 
