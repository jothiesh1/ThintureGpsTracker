package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;


@Controller
public class  AlertAdminController {

	
	@GetMapping("/alert_admin")
    public String createdevices(Model model) {
        return "alert_admin"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
	@GetMapping("/alert_dealer")
    public String createDealerAlert(Model model) {
        return "alert_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
	@GetMapping("/alert_client")
    public String clientAlert(Model model) {
        return "alert_client"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}