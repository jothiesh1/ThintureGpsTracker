package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;


	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.stereotype.Controller;
	import org.springframework.web.bind.annotation.GetMapping;

	@Controller
	public class dealerPageController {

	    private static final Logger logger = LoggerFactory.getLogger(dealerPageController.class);

	    @GetMapping("/dashboard_dealer")
	    public String dashboardDealer() {
	        logger.info("🔹 Dealer - Dashboard");
	        return "dealer/dashboard_dealer";
	    }

	    @GetMapping("/addclient_dealer")
	    public String addClient() {
	        return "dealer/addclient_dealer";
	    }

	    @GetMapping("/adduser_dealer")
	    public String addUser() {
	        return "dealer/adduser_dealer";
	    }

	    @GetMapping("/adddriver_dealer")
	    public String addDriver() {
	        return "dealer/adddriver_dealer";
	    }

	    @GetMapping("/add_dealer_device_to_client")
	    public String assignDeviceToClient() {
	        return "dealer/add_dealer_device_to_client";
	    }

	    @GetMapping("/addrfid_dealer_client")
	    public String assignRfidToClient() {
	        return "dealer/addrfid_dealer_client";
	    }

	    @GetMapping("/mapDealer")
	    public String liveMap() {
	        return "dealer/mapDealer";
	    }

	    @GetMapping("/playback_dealer")
	    public String playbackTrack() {
	        return "dealer/playback_dealer";
	    }

	    @GetMapping("/installationDealer")
	    public String installation() {
	        return "dealer/installationDealer";
	    }

	    @GetMapping("/event_dealer")
	    public String eventReport() {
	        return "dealer/event_dealer";
	    }

	    @GetMapping("/dealer_device_report")
	    public String deviceReport() {
	        return "dealer/dealer_device_report";
	    }

	    @GetMapping("/view_dealer_complaints")
	    public String complaintReport() {
	        return "dealer/view_dealer_complaints";
	    }

	    @GetMapping("/client_view_dealer")
	    public String clientView() {
	        return "dealer/client_view_dealer";
	    }

	    @GetMapping("/users_view_dealer")
	    public String userView() {
	        return "dealer/users_view_dealer";
	    }

	    @GetMapping("/dealer_driver_view")
	    public String driverView() {
	        return "dealer/dealer_driver_view";
	    }

	    @GetMapping("/alert_dealer")
	    public String dealerAlerts() {
	        return "dealer/alert_dealer";
	    }

	    @GetMapping("/dealer_support")
	    public String registerComplaint() {
	        return "dealer/dealer_support";
	    }

	    @GetMapping("/dealer_eventreport")
	    public String dealerEventRedirect() {
	        return "dealer/dealer_eventreport";
	    }

	    @GetMapping("/dealer_vehicle_violation")
	    public String dealerVehicleViolation() {
	        return "dealer/dealer_vehicle_violation";
	    }

	    @GetMapping("/driver_violation")
	    public String dealerDriverViolation() {
	        return "dealer/driver_violation";
	    }
	

	
	
	@GetMapping("/dealerDetails")
    public String dealerDetailShow(Model model) {
        return "dealer/dealerDetails"; // Should match the name of the HTML file in the templates folder (map2.html)
    }

	
	
	 
	  
}





