package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class dealerPageController {
	
	
	
	@GetMapping("/dealerDetails")
    public String dealerDetailShow(Model model) {
        return "dealerDetails"; // Should match the name of the HTML file in the templates folder (map2.html)
    }

	@GetMapping("/dealer_device_report")
    public String dealerDevice(Model model) {
        return "dealer_device_report"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	@GetMapping("/dealer_eventreport")
    public String createdevices(Model model) {
        return "dealer_eventreport"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
	
	




	  
	
	  @GetMapping("/users_view_dealer")
	    public String showAdminViewClient(Model model) {
	      
	       
	        return "users_view_dealer"; // HTML form for adding an admin file name is add_admin.html
	    }


	  @GetMapping("/client_view_dealer")
	    public String showAdminViewDealer(Model model) {
		
	        return "client_view_dealer"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  
	 
	  
}





