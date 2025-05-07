package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminPageController {

    private static final Logger logger = LoggerFactory.getLogger(adminPageController.class);

    @GetMapping("/admin/dashboard")
    public String dashboardAdmin() {
        logger.info("🔹 Accessed Admin - Dashboard Page");
        return "admin/dashboard_admin";
    }

    @GetMapping("/admin/adddealer")
    public String addDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add Dealer Page");
        return "admin/adddealer_admin";
    }

    @GetMapping("/admin/addclient")
    public String addClientAdmin() {
        logger.info("🔹 Accessed Admin - Add Client Page");
        return "admin/addclient_admin";
    }

    @GetMapping("/admin/adduser")
    public String addUserAdmin() {
        logger.info("🔹 Accessed Admin - Add User Page");
        return "admin/adduser_admin";
    }

    @GetMapping("/admin/adddriver")
    public String addDriverAdmin() {
        logger.info("🔹 Accessed Admin - Add Driver Page");
        return "admin/adddriver_admin";
    }

    @GetMapping("/admin/add_devices_dealer")
    public String addDevicesDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add Devices to Dealer");
        return "admin/add_devices_dealer_admin";
    }

    @GetMapping("/admin/add_devices_client")
    public String addDevicesClientAdmin() {
        logger.info("🔹 Accessed Admin - Add Devices to Client");
        return "admin/add_devices_client_admin";
    }

    @GetMapping("/admin/addrfid_dealer")
    public String addRfidDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add RFID to Dealer");
        return "admin/addrfid_admin_dealer";
    }

    @GetMapping("/admin/addrfid_client")
    public String addRfidClientAdmin() {
        logger.info("🔹 Accessed Admin - Add RFID to Client");
        return "admin/addrfid_admin_client";
    }

    @GetMapping("/admin/map")
    public String mapAdmin() {
        logger.info("🔹 Accessed Admin - Live Map");
        return "admin/map_admin";
    }

    @GetMapping("/admin/replay")
    public String replayAdmin() {
        logger.info("🔹 Accessed Admin - Playback Track");
        return "admin/replay_admin";
    }
    
    @GetMapping("/admin/playback")
    public String playbackAdmin() {
        logger.info("🔹 Accessed Admin - Playback Track");
        return "admin/playback_admin";
    }


    @GetMapping("/admin/install")
    public String installAdmin() {
        logger.info("🔹 Accessed Admin - Install Page");
        return "admin/install_admin";
    }

    @GetMapping("/admin/install_report")
    public String installReportAdmin() {
        logger.info("🔹 Accessed Admin - Installation Report");
        return "admin/install_report_admin";
    }

    @GetMapping("/admin/event")
    public String eventAdmin() {
        logger.info("🔹 Accessed Admin - Event & Violations Report");
        return "admin/event_admin";
    }

    @GetMapping("/admin/event_report")
    public String eventAdminReport() {
        logger.info("🔹 Accessed Admin - Event & Violations Report");
        return "admin/event_report_admin";
    }

    @GetMapping("/admin/alert")
    public String alertAdmin() {
        logger.info("🔹 Accessed Admin - Alerts Notification Page");
        return "admin/alert_admin";
    }

    @GetMapping("/admin/dealer_view")
    public String dealerViewAdmin() {
        logger.info("🔹 Accessed Admin - Dealer Management View");
        return "admin/dealer_view_admin";
    }

    @GetMapping("/admin/client_view")
    public String clientViewAdmin() {
        logger.info("🔹 Accessed Admin - Client Management View");
        return "admin/client_view_admin";
    }

    @GetMapping("/admin/user_view")
    public String userViewAdmin() {
        logger.info("🔹 Accessed Admin - User Management View");
        return "admin/user_view_admin";
    }

    @GetMapping("/admin/driver_view")
    public String driverViewAdmin() {
        logger.info("🔹 Accessed Admin - Driver Management View");
        return "admin/admin_view_driver";
    }

    @GetMapping("/admin/view_complaints")
    public String viewAdminComplaints() {
        logger.info("🔹 Accessed Admin - View Complaints Page");
        return "admin/view_admin_complaints";
    }

    @GetMapping("/admin/vehicle_violation")
    public String vehicleViolationAdmin() {
        logger.info("🔹 Accessed Admin - Vehicle Violation Report Page");
        return "admin/vehicle_violation_admin";
    }
    
    @GetMapping("/admin/profile_admin")
    public String loadAdminProfilePage() {
        return "admin/profile_admin"; // not profile_admin.html
    }

}