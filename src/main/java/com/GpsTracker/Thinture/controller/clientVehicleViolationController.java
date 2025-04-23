package com.GpsTracker.Thinture.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
@Controller
public class clientVehicleViolationController {

	 @GetMapping("/client_vehicle_violation")
	    public String showClientVehicleReport() {
	        return "client_vehicle_violation"; // Assuming you are using Thymeleaf templates (place in /templates)
	    }
	
}
