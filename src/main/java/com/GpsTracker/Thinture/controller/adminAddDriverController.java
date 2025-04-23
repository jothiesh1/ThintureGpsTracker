package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminAddDriverController {

	
	@GetMapping("/adddriver_admin")
    public String adminAddDriver(Model model) {
        return "adddriver_admin"; 
    }
}












