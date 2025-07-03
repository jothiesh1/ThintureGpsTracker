package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/superadmin")
public class SuperAdminController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Ensure there's a dashboard.html file under src/main/resources/templates
    }
}

