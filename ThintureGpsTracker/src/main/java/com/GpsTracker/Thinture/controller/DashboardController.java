package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";  // Ensure this matches the path in the templates directory
    }
    @GetMapping("/total_users")
    public String getTotalUsers() {
        return "total_users";  // Ensure this matches the path in the templates directory
    }
    
    @GetMapping("/details_user")
    public String getDetailsUser() {
        return "details_user";  // Ensure this matches the path in the templates directory
    }
    @GetMapping("/createdevices")
    public String getCreateDevices() {
        return "createdevices";  // Ensure this matches the path in the templates directory
    }
    // Mapping to show the add driver form
    @GetMapping("/add_driver")
    public String showAddDriverPage() {
       // logger.info("Navigating to add_driver.html page.");
        return "add_driver"; // This should match the name of your HTML file (without the .html extension)
    }
}
/*
 * @GetMapping("/total_vehicles") public String getTotalVehicles () { return
 * "total_vehicles"; // Ensure this matches the path in the templates directory
 * }
 * 
 * }
 */
