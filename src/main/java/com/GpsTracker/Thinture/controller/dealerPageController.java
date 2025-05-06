package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dealerPageController {

    private static final Logger logger = LoggerFactory.getLogger(dealerPageController.class);

    @GetMapping("/dealer/dashboard")
    public String dashboardDealer() {
        logger.info("🔹 Dealer - Dashboard");
        return "dealer/dashboard_dealer";
    }

    @GetMapping("/dealer/addclient")
    public String addClient() {
        logger.info("🔹 Dealer - Add Client");
        return "dealer/addclient_dealer";
    }

    @GetMapping("/dealer/adduser")
    public String addUser() {
        logger.info("🔹 Dealer - Add User");
        return "dealer/adduser_dealer";
    }

    @GetMapping("/dealer/adddriver")
    public String addDriver() {
        logger.info("🔹 Dealer - Add Driver");
        return "dealer/adddriver_dealer";
    }

    @GetMapping("/dealer/add_device_to_client")
    public String assignDeviceToClient() {
        logger.info("🔹 Dealer - Add Device to Client");
        return "dealer/add_dealer_device_to_client";
    }

    @GetMapping("/dealer/addrfid_to_client")
    public String assignRfidToClient() {
        logger.info("🔹 Dealer - Add RFID to Client");
        return "dealer/addrfid_dealer_client";
    }

    @GetMapping("/dealer/map")
    public String liveMap() {
        logger.info("🔹 Dealer - Live Map");
        return "dealer/mapDealer";
    }

    @GetMapping("/dealer/playback")
    public String playbackTrack() {
        logger.info("🔹 Dealer - Playback");
        return "dealer/playback_dealer";
    }

    @GetMapping("/dealer/installation")
    public String installation() {
        logger.info("🔹 Dealer - Installation");
        return "dealer/installationDealer";
    }

    @GetMapping("/dealer/event")
    public String eventReport() {
        logger.info("🔹 Dealer - Event Report");
        return "dealer/event_dealer";
    }

    @GetMapping("/dealer/device_report")
    public String deviceReport() {
        logger.info("🔹 Dealer - Device Report");
        return "dealer/dealer_device_report";
    }

    @GetMapping("/dealer/view_complaints")
    public String complaintReport() {
        logger.info("🔹 Dealer - Complaints");
        return "dealer/view_dealer_complaints";
    }

    @GetMapping("/dealer/client_view")
    public String clientView() {
        logger.info("🔹 Dealer - Client View");
        return "dealer/client_view_dealer";
    }

    @GetMapping("/dealer/user_view")
    public String userView() {
        logger.info("🔹 Dealer - User View");
        return "dealer/users_view_dealer";
    }

    @GetMapping("/dealer/driver_view")
    public String driverView() {
        logger.info("🔹 Dealer - Driver View");
        return "dealer/dealer_driver_view";
    }

    @GetMapping("/dealer/alerts")
    public String dealerAlerts() {
        logger.info("🔹 Dealer - Alerts");
        return "dealer/alert_dealer";
    }

    @GetMapping("/dealer/support")
    public String registerComplaint() {
        logger.info("🔹 Dealer - Support");
        return "dealer/dealer_support";
    }

    @GetMapping("/dealer/event_report")
    public String dealerEventRedirect() {
        logger.info("🔹 Dealer - Event Report Redirect");
        return "dealer/dealer_eventreport";
    }

    @GetMapping("/dealer/vehicle_violation")
    public String dealerVehicleViolation() {
        logger.info("🔹 Dealer - Vehicle Violation");
        return "dealer/dealer_vehicle_violation";
    }

    @GetMapping("/dealer/driver_violation")
    public String dealerDriverViolation() {
        logger.info("🔹 Dealer - Driver Violation");
        return "dealer/driver_violation";
    }

    @GetMapping("/dealer/details")
    public String dealerDetailShow(org.springframework.ui.Model model) {
        logger.info("🔹 Dealer - Details");
        return "dealer/dealerDetails";
    }
    
    @GetMapping("/dealer/profile_dealer")
    public String loadDealerProfilePage() {
        return "dealer/profile_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
    }

}
