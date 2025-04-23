package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;










@Controller
public class dealerDeviceReportController {

	
	@GetMapping("/dealer_device_report")
    public String dealerDevice(Model model) {
        return "dealer_device_report"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}



