package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
@Controller
public class ViolationController {

	 @GetMapping("/vehicle_report")
	    public String showVehicleReport() {
	        return "vehicle_report"; // Assuming you are using Thymeleaf templates (place in /templates)
	    }
	
}
