package com.GpsTracker.Thinture.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminEventReportController {

	
	@GetMapping("/event_report_admin")
    public String adminEventReport(Model model) {
        return "event_report_admin"; 
    }
}








