package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class clientPageController {

    private static final Logger logger = LoggerFactory.getLogger(clientPageController.class);

    @GetMapping("/client/dashboard_client")
    public String dashboard(Model model) {
        logger.info("📊 Loading dashboard_client.html");
        return "client/dashboard_client";
    }

    @GetMapping("/client/adduser_client")
    public String addUserClient(Model model) {
        logger.info("➕ Loading adduser_client.html");
        return "client/adduser_client";
    }

    @GetMapping("/client/add_driver_client")
    public String addDriverClient(Model model) {
        logger.info("🚗 Loading add_driver_client.html");
        return "client/add_driver_client";
    }

    @GetMapping("/client/alert_client")
    public String alertClient(Model model) {
        logger.info("🔔 Loading alert_client.html");
        return "client/alert_client";
    }

    @GetMapping("/client/clientMap")
    public String clientMap(Model model) {
        logger.info("🗺️ Loading clientMap.html");
        return "client/clientMap";
    }

    @GetMapping("/client/client_support")
    public String clientSupport(Model model) {
        logger.info("📨 Loading client_support.html");
        return "client/client_support";
    }

    @GetMapping("/client/client_eventreport")
    public String clientEventReport(Model model) {
        logger.info("📄 Loading client_eventreport.html");
        return "client/client_eventreport";
    }

    @GetMapping("/client/client_vehicle_violation")
    public String clientVehicleViolation(Model model) {
        logger.info("🚦 Loading client_vehicle_violation.html");
        return "client/client_vehicle_violation";
    }

    @GetMapping("/client/client_device_report")
    public String clientDeviceReport(Model model) {
        logger.info("🧾 Loading client_device_report.html");
        return "client/client_device_report";
    }

    @GetMapping("/client/driver_view_client")
    public String driverViewClient(Model model) {
        logger.info("👨‍✈️ Loading driver_view_client.html");
        return "client/driver_view_client";
    }

    @GetMapping("/client/event_client")
    public String eventClient(Model model) {
        logger.info("⚠️ Loading event_client.html");
        return "client/event_client";
    }

    @GetMapping("/client/eventreport_client")
    public String eventReportClient(Model model) {
        logger.info("📑 Loading eventreport_client.html");
        return "client/eventreport_client";
    }

    @GetMapping("/client/installationClient")
    public String installationClient(Model model) {
        logger.info("🔧 Loading installationClient.html");
        return "client/installationClient";
    }

    @GetMapping("/client/playback_client")
    public String playbackClient(Model model) {
        logger.info("⏪ Loading playback_client.html");
        return "client/playback_client";
    }

    @GetMapping("/client/user_view_client")
    public String userViewClient(Model model) {
        logger.info("👤 Loading user_view_client.html");
        return "client/user_view_client";
    }

    @GetMapping("/client/users_view_client")
    public String usersViewClient(Model model) {
        logger.info("👥 Loading users_view_client.html");
        return "client/users_view_client";
    }

    @GetMapping("/client/view_client_complaints")
    public String viewClientComplaints(Model model) {
        logger.info("📝 Loading view_client_complaints.html");
        return "client/view_client_complaints";
    }
    @GetMapping("/client/profile_client")
    public String viewClientpROFILE(Model model) {
        logger.info("📝 Loading view_client_complaints.html");
        return "client/profile_client";
    }
}
