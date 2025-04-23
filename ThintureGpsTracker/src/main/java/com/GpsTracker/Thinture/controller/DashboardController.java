package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/dashboard")
    public String superadmin() {
        logger.info("🧭 Opening SUPERADMIN dashboard");
        return "dashboard"; // Make sure dashboard.html exists
    }

    @GetMapping("/dashboard_admin")
    public String admin() {
        logger.info("🧭 Opening ADMIN dashboard");
        return "dashboard_admin"; // Make sure dashboard_admin.html exists
    }

    @GetMapping("/dashboard_dealer")
    public String dealer() {
        logger.info("🧭 Opening DEALER dashboard");
        return "dashboard_dealer";
    }

    @GetMapping("/dashboard_user")
    public String user() {
        logger.info("🧭 Opening USER dashboard");
        return "dashboard_user";
    }

    @GetMapping("/dashboard_client")
    public String client() {
        logger.info("🧭 Opening CLIENT dashboard");
        return "dashboard_client";
    }
}
