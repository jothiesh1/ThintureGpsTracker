package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;




@Controller
public class installDealerController {


	  @GetMapping("/installationDealer")
	    public String createdevices(Model model) {
	        return "installationDealer"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  @GetMapping("/driver_view")
		public String driverViewShow(Model model) {
		    return "driver_view" ;// Should match the name of the HTML file in the templates folder (map2.html)
		}
}
