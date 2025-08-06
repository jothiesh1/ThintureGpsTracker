package com.GpsTracker.Thinture.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminAddClientController {

	
	@GetMapping("/addclient_admin")
    public String addClientAdmin(Model model) {
        return "addclient_admin"; 
    }
}

