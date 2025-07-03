package com.GpsTracker.Thinture.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminInstallController {

	
	@GetMapping("/install_admin")
    public String adminInstall(Model model) {
        return "install_admin"; 
    }
}





