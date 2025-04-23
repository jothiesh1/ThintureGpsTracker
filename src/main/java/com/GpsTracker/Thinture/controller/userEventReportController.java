package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;




@Controller
public class userEventReportController{
	  @GetMapping("/user_event_report")
	    public String showUserEventReportController(Model model) {
	        return "user_event_report" ;// Should match the name of the HTML file in the templates folder (map2.html)
	    }
}

