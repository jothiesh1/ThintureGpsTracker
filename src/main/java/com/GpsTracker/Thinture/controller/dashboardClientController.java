package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ch.qos.logback.core.model.Model;
@Controller
public class dashboardClientController {	
	@GetMapping("/dashboard_client")
    public String dashboardClientShow(Model model) {
        return "dashboard_client"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}



