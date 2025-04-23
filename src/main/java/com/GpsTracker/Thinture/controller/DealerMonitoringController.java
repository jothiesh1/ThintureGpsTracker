package com.GpsTracker.Thinture.controller;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.service.VehicleLastLocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
public class DealerMonitoringController {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private VehicleLastLocationService vehicleLastLocationService;  // Inject the service

    private static final Logger logger = LoggerFactory.getLogger(DealerMonitoringController.class);

    // Show the dealer login page (GET method)
    @GetMapping("/dealer_login")
    public String showDealerLoginPage() {
        logger.info("Rendering dealer login page.");
        return "dealer_monitoring";  // Return the dealer_monitoring.html view
    }

    // Handle the dealer login submission (POST method)
    @PostMapping("/dealer_login")
    public String handleDealerLogin(@RequestParam("username") String username, 
                                    @RequestParam("password") String password, 
                                    Model model) {
        logger.info("Attempting login for dealer with username: {}", username);

        Dealer dealer = dealerRepository.findByEmailAndPassword(username, password);
        if (dealer != null) {
            logger.info("Login successful for dealer: {} with ID: {}", dealer.getCompanyName(), dealer.getId());
            model.addAttribute("dealer", dealer);

            // Fetch last known locations of vehicles
            List<VehicleLastLocation> lastKnownLocations = vehicleLastLocationService.getAllLastKnownLocations();
            model.addAttribute("lastKnownLocations", lastKnownLocations);

            return "dealer_monitoring_dashboard";  // Pass the data to this HTML view
        } else {
            logger.error("Login failed for username: {}", username);
            model.addAttribute("error", "Invalid username or password.");
            return "login";  // Return to login page with error
        }
    }

    // Show the dealer monitoring dashboard after login
    @GetMapping("/dealer_monitoring")
    public String showDealerMonitoring(@RequestParam("dealerId") Long dealerId, Model model) {
        logger.info("Rendering dealer monitoring dashboard for dealerId: {}", dealerId);

        Dealer dealer = dealerRepository.findById(dealerId).orElse(null);
        if (dealer != null) {
            model.addAttribute("dealer", dealer);

            // Fetch last known locations of vehicles
            List<VehicleLastLocation> lastKnownLocations = vehicleLastLocationService.getAllLastKnownLocations();
            model.addAttribute("lastKnownLocations", lastKnownLocations);

            return "dealer_monitoring_dashboard";
        } else {
            logger.error("Dealer not found with ID: {}", dealerId);
            model.addAttribute("error", "Dealer not found.");
            return "login";
        }
    }
}
