package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViolationReportViewController {

    // Mapping to serve the vehicle_violation.html
    @GetMapping("/vehicle-violations")
    public String showViolationPage() {
        // This will look for vehicle_violation.html in the 'templates' folder
        return "vehicle_violation";
    }
}