package com.GpsTracker.Thinture.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.GpsTracker.Thinture.model.Admin;

import ch.qos.logback.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class adminPageViewController {


	private static final Logger logger = LoggerFactory.getLogger(adminPageViewController.class);
	/*
	 
	 */
	  
	
	  @GetMapping("/client_view_admin")
	    public String showAdminViewClient(Model model) {
	        logger.info("Displaying the client_view_admin");
	       
	        return "client_view_admin"; // HTML form for adding an admin file name is add_admin.html
	    }


	  @GetMapping("/dealer_view_admin")
	    public String showAdminViewDealer(Model model) {
		  logger.info("Admin login Displaying theuser_view_admin");
	        return "dealer_view_admin"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  
	  @GetMapping("/user_view_admin")
	    public String dealerShowAdminView() {
		  logger.info(" Admin loginDisplaying the user_view_admin");
	        return "user_view_admin"; // This should match your HTML file in templates
	    }
	
	  

	  @GetMapping("/admin_view_driver")
	    public String driverShowAdminView() {
		  logger.info(" Admin loginDisplaying the admin_view_driver");
	        return "admin_view_driver"; // This should match your HTML file in templates
	    }
}

