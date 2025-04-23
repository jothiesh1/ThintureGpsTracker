package com.GpsTracker.Thinture.controller;







import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class dashboardAdminController {

	
	@GetMapping("/dashboard_admin")
    public String dashboardAdminOpen(Model model) {
        return "dashboard_admin"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}



















