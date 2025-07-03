package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class clientEventReportController {

	
	@GetMapping("/client_eventreport")
    public String eventDealerF(Model model) {
        return "client_eventreport"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}


