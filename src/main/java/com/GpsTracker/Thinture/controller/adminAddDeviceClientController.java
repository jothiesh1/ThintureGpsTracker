package com.GpsTracker.Thinture.controller;





import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminAddDeviceClientController {

	
	@GetMapping("/add_devices_client_admin")
    public String adminAddDeviceClient(Model model) {
        return "add_devices_client_admin"; 
    }
}










