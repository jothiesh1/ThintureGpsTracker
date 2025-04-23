package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class addRfidtoDealercontroller {

	
	@GetMapping("/add_rfidto_dealer")
    public String createdevi(Model model) {
        return "add_rfidto_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
	
	
	
}


