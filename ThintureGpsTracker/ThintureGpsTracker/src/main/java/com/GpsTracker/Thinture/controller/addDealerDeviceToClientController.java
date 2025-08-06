package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class addDealerDeviceToClientController {
	
	 @GetMapping("/add_dealer_device_to_client")
	    public String showMap(Model model) {
	        // Add any attributes to the model if necessary
	        return "add_dealer_device_to_client"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    }

}

