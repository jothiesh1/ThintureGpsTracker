package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class dealerDetails {

	
	@GetMapping("/dealerDetails")
    public String createdevices(Model model) {
        return "dealerDetails"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
