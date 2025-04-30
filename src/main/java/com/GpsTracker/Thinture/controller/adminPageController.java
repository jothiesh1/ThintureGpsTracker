package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminPageController {

    private static final Logger logger = LoggerFactory.getLogger(adminPageController.class);

    @GetMapping("/dashboard_admin")
    public String dashboardAdmin() {
        logger.info("🔹 Accessed Admin - Dashboard Page");
        return "admin/dashboard_admin";
    }

    @GetMapping("/adddealer_admin")
    public String addDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add Dealer Page");
        return "admin/adddealer_admin";
    }

    @GetMapping("/addclient_admin")
    public String addClientAdmin() {
        logger.info("🔹 Accessed Admin - Add Client Page");
        return "admin/addclient_admin";
    }

    @GetMapping("/adduser_admin")
    public String addUserAdmin() {
        logger.info("🔹 Accessed Admin - Add User Page");
        return "admin/adduser_admin";
    }

    @GetMapping("/adddriver_admin")
    public String addDriverAdmin() {
        logger.info("🔹 Accessed Admin - Add Driver Page");
        return "admin/adddriver_admin";
    }

    @GetMapping("/add_devices_dealer_admin")
    public String addDevicesDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add Devices to Dealer");
        return "admin/add_devices_dealer_admin";
    }

    @GetMapping("/add_devices_client_admin")
    public String addDevicesClientAdmin() {
        logger.info("🔹 Accessed Admin - Add Devices to Client");
        return "admin/add_devices_client_admin";
    }

    @GetMapping("/addrfid_admin_dealer")
    public String addRfidDealerAdmin() {
        logger.info("🔹 Accessed Admin - Add RFID to Dealer");
        return "admin/addrfid_admin_dealer";
    }

    @GetMapping("/addrfid_admin_client")
    public String addRfidClientAdmin() {
        logger.info("🔹 Accessed Admin - Add RFID to Client");
        return "admin/addrfid_admin_client";
    }

    
    

    @GetMapping("/map_admin")
    public String mapAdmin() {
        logger.info("🔹 Accessed Admin - Live Map");
        return "admin/map_admin";
    }

    @GetMapping("/replay_admin")
    public String replayAdmin() {
        logger.info("🔹 Accessed Admin - Playback Track");
        return "admin/replay_admin";
    }

    @GetMapping("/install_admin")
    public String installAdmin() {
        logger.info("🔹 Accessed Admin - Install Page");
        return "admin/install_admin";
    }

    @GetMapping("/install_report_admin")
    public String installReportAdmin() {
        logger.info("🔹 Accessed Admin - Installation Report");
        return "admin/install_report_admin";
    }

    @GetMapping("/event_admin")
    public String eventAdmin() {
        logger.info("🔹 Accessed Admin - Event & Violations Report");
        return "admin/event_admin";
    }
    

    @GetMapping("/event_report_admin")
    public String eventAdminReport() {
        logger.info("🔹 Accessed Admin - Event & Violations Report");
        return "admin/event_report_admin";
    }

    @GetMapping("/alert_admin")
    public String alertAdmin() {
        logger.info("🔹 Accessed Admin - Alerts Notification Page");
        return "admin/alert_admin";
    }

    @GetMapping("/dealer_view_admin")
    public String dealerViewAdmin() {
        logger.info("🔹 Accessed Admin - Dealer Management View");
        return "admin/dealer_view_admin";
    }

    @GetMapping("/client_view_admin")
    public String clientViewAdmin() {
        logger.info("🔹 Accessed Admin - Client Management View");
        return "admin/client_view_admin";
    }

    @GetMapping("/user_view_admin")
    public String userViewAdmin() {
        logger.info("🔹 Accessed Admin - User Management View");
        return "admin/user_view_admin";
    }

    @GetMapping("/admin_view_driver")
    public String driverViewAdmin() {
        logger.info("🔹 Accessed Admin - Driver Management View");
        return "admin/admin_view_driver";
    }

    @GetMapping("/view_admin_complaints")
    public String viewAdminComplaints() {
        logger.info("🔹 Accessed Admin - View Complaints Page");
        return "admin/view_admin_complaints";
    }

    @GetMapping("/vehicle_violation_admin")
    public String vehicleViolationAdmin() {
        logger.info("🔹 Accessed Admin - Vehicle Violation Report Page");
        return "admin/vehicle_violation_admin";
    }
}
