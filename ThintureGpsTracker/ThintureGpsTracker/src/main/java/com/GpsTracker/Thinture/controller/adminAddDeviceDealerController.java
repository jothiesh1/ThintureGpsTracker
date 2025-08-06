package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminAddDeviceDealerController {

	
	@GetMapping("/add_devices_dealer_admin")
    public String adminAddDeviceDealer(Model model) {
        return "add_devices_dealer_admin"; 
    }
}










