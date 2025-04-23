package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class adminAddDealerController {

	
	@GetMapping("/adddealer_admin")
    public String dealerAdmin(Model model) {
        return "adddealer_admin"; 
    }
}


