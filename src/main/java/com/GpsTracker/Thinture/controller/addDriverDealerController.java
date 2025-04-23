package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;




import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class addDriverDealerController {


	private static final Logger logger = LoggerFactory.getLogger(addDriverDealerController.class);




@GetMapping("/adddriver_dealer")
public String showAdddriverDealer(Model model) {
	  logger.info("Displaying the adddriver_dealer .html ");
	
    return "adddriver_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
}





@GetMapping("/adddriver_client")
public String showAdddriverclient(Model model) {
	 logger.info("Displaying the adddriver_CLIENT .html ");
    return "adddriver_client"; // Should match the name of the HTML file in the templates folder (map2.html)
}


}
