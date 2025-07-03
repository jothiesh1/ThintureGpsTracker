package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.GpsTracker.Thinture.model.Admin;

import ch.qos.logback.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class SuperAdminPageController {


	private static final Logger logger = LoggerFactory.getLogger(SuperAdminPageController.class);
	/*
	 
	 */
	  
	  @GetMapping("/add_admin")
	    public String showAddAdminForm(Model model) {
	        logger.info("Displaying the Add Admin Form");
	       
	        return "add_admin"; // HTML form for adding an admin file name is add_admin.html
	    }


	  @GetMapping("/add_client")
	    public String showAddClientForm(Model model) {
		  logger.info("SuperAdmin login Displaying the Add client Form");
	        return "add_client"; // Should match the name of the HTML file in the templates folder (add_client.html)
	    }
	  
	  @GetMapping("/add_dealer")
	    public String showAddDealerForm() {
		  logger.info(" SuperAdmin loginDisplaying the Add dealer Form");
	        return "add_dealer"; // This should match your HTML file in templates
	    }
	  
	  @GetMapping("/add_driver")
	    public String showAddDriverForm() {
		  logger.info("SuperAdmin login Displaying the Add driver Form");
	        return "add_driver"; //Return the HTML form for adding add_driver.html
	    }
	    
	  
	  @GetMapping("/add_user")
	    public String showAddUserForm(Model model) {
	        logger.info("SuperAdmin login Displaying the Add User Form");
	        return "add_user"; // Return the HTML form for adding add_user.html
	    }
	  
	  @GetMapping("/Add_devices_dealer")
	    public String showAddUserAdd_devices_dealer_Form(Model model) {
		  logger.info("SuperAdmin login Displaying the Add_devices_dealer.html Form");
	        return "Add_devices_dealer"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    }
	    
	  
	  @GetMapping("/Add_devices_client")
	    public String showAdd_devices_client(Model model) {
		  logger.info("SuperAdmin login Displaying the  Add_devices_client.html Form");
	        // Add any attributes to the model if necessary
	        return "Add_devices_client"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    } 
	  
	  @GetMapping("/map4")
	    public String showMapsuper(Model model) {
		  logger.info("SuperAdmin login Displaying the  map4.html ");
	        return "map4"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  

	    @GetMapping("/playback")
	    public String showplayback(Model model) {
	    	  logger.info("SuperAdmin login Displaying the playback .html ");
	        return "playback"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
	    }
	 
	    
	    @GetMapping("/replay")
	    public String replayPage() {
	    	  logger.info("SuperAdmin login Displaying the replay .html ");
	        return "replay"; // This refers to replay.html in the templates directory
	    }
	    
	    @GetMapping("/map2")
	    public String showMap2(Model model) {
	    	logger.info("SuperAdmin login Displaying the map2 .html report");
	        return "map2"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	    
	    @GetMapping("/createdevices")
	    public String createdevices(Model model) {
	    	logger.info("SuperAdmin login Displaying the  createdevices.html ");
	        return "createdevices"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	    
	    @GetMapping("/event")
	    public String showEventModel(Model model) {
	    	logger.info("SuperAdmin login Displaying the  event.html report ");
	        return "event"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	    
	    @GetMapping("/eventreport")
	    public String showEventReport(Model model) {
	    	logger.info("SuperAdmin login Displaying the  eventreport.html report ");
	        return "eventreport"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	    
	    
	    
	    
	    @GetMapping("/device_report")
	    public String deviceReportShow() {
	    	logger.info("SuperAdmin login Displaying the  device_report.html report ");
	        return "device_report"; // This refers to replay.html in the templates directory
	    }

	    
	    
	    @GetMapping("/vehicle_violation")
	    public String showVehicleReport() {
	    	logger.info("SuperAdmin login Displaying the  vehicle_violation.html report ");
	        return "vehicle_violation"; // Assuming you are using Thymeleaf templates (place in /templates)
	    }
	    
	    
	    
	    
}
	  
	  

