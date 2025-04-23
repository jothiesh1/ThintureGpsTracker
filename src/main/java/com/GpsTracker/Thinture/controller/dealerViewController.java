package com.GpsTracker.Thinture.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.GpsTracker.Thinture.model.Admin;

import ch.qos.logback.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class dealerViewController {


	private static final Logger logger = LoggerFactory.getLogger(dealerViewController.class);
	/*
	 
	 */
	  
	
	  @GetMapping("/users_view_dealer")
	    public String showAdminViewClient(Model model) {
	        logger.info("Displaying the client_view_dealer.html");
	       
	        return "users_view_dealer"; // HTML form for adding an admin file name is add_admin.html
	    }


	  @GetMapping("/client_view_dealer")
	    public String showAdminViewDealer(Model model) {
		  logger.info("Admin login Displaying client_view_dealer");
	        return "client_view_dealer"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  
	

	  @GetMapping("/dealer_driver_view")
	    public String showDriverViewDealer(Model model) {
		  logger.info("Admin login Displaying dealer_driver_view");
	        return "dealer_driver_view"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  

	  
	  
}

