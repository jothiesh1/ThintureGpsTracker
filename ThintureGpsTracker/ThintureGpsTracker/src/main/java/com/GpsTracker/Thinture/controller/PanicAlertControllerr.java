package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Controller
public class PanicAlertControllerr {
	 @GetMapping("/panic")
	 public String showMap(Model model) {
	        // Add any attributes to the model if necessary
	        return "panic_alert"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    }
}