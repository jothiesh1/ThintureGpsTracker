package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class clientViewController {


	private static final Logger logger = LoggerFactory.getLogger(clientViewController.class);
	
	

	  @GetMapping("/users_view_client")
	    public String showUsertViewClient(Model model) {
		  logger.info("Admin login Displaying dealerViewController and users_view_client.html ");
	        return "users_view_client"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  
	  
	  

	  @GetMapping("/driver_view_client")
	    public String showDriverViewClient(Model model) {
		  logger.info("Admin login Displaying dealerViewController and users_view_client.html ");
	        return "driver_view_client"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
}
