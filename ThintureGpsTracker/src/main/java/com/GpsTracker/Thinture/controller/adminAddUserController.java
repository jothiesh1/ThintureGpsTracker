package com.GpsTracker.Thinture.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminAddUserController {

	
	@GetMapping("/adduser_admin")
    public String adduserAdmin(Model model) {
        return "adduser_admin"; 
    }
}
