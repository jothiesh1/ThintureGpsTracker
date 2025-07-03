package com.GpsTracker.Thinture.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class clientDeviceReportController {

	
	@GetMapping("/client_device_report")
    public String eventDealerF(Model model) {
        return "client_device_report"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}






















