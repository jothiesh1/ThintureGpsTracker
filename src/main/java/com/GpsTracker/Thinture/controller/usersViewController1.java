package com.GpsTracker.Thinture.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class usersViewController1 {

	
	
	
	
	
	
	
	
	
	 @GetMapping("/users_view_client")
	 public String showUserClient(Model model) {
	        // Add any attributes to the model if necessary
	        return "users_view_client"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    }
}
