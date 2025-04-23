package com.GpsTracker.Thinture.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class clientMapController{
	  @GetMapping("/clientMap")
	    public String showMap2(Model model) {
	        return "clientMap" ;// Should match the name of the HTML file in the templates folder (map2.html)
	    }
}
