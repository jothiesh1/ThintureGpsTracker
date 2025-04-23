package com.GpsTracker.Thinture.controller;






import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;





@Controller
public class addRfidToClientController {

	
	@GetMapping("/add_rfidto_client")
    public String createdevil(Model model) {
        return "/add_rfidto_client"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
	
	@GetMapping("/dashboard")
    public String maindash(Model model) {
        return "/dashboard"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
}


