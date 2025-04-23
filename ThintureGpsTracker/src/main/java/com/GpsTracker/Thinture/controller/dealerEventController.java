package com.GpsTracker.Thinture.controller;






import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class dealerEventController {

	
	@GetMapping("/dealer_eventreport")
    public String createdevices(Model model) {
        return "dealer_eventreport"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}



