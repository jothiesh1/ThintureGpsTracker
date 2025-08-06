package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminEventController {

	
	@GetMapping("/event_admin")
    public String adminEvent(Model model) {
        return "event_admin"; 
    }
}







