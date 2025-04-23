package com.GpsTracker.Thinture.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    // Mapping to serve the Driver_report.html
    @GetMapping("/driver-report")
    public String showDriverReport() {
        return "Driver_report"; // This refers to Driver_report.html in the /static folder
    }
}
