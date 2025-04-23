package com.GpsTracker.Thinture.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



@Controller
public class userEventController{
	  @GetMapping("/user_event")
	    public String showuserEvent(Model model) {
	        return "user_event" ;// Should match the name of the HTML file in the templates folder (map2.html)
	    }
}


