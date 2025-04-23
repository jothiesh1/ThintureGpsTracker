package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;







@Controller
public class userDashbaordController {

	
	@GetMapping("/dashboard_user")
    public String createdevices(Model model) {
        return "dashboard_user"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
