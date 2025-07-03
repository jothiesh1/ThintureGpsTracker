package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
@Controller
public class dealerVehicleViolationController {

	 @GetMapping("/dealer_vehicle_violation")
	    public String showDealerVehicleReport() {
	        return "dealer_vehicle_violation"; // Assuming you are using Thymeleaf templates (place in /templates)
	    }
	
}
