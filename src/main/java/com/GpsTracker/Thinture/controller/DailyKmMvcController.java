package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class DailyKmMvcController {
	 @GetMapping("/daily_km_chart")
	    public String dailyShow(Model model) {
	        return "daily_km_chart"; 
	    }
}
