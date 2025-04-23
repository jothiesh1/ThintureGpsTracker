package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class mapDealerController {

	
	@GetMapping("/mapDealer")
    public String createdevices(Model model) {
        return "mapDealer"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
