package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class chart_pie {

	
	@GetMapping("/chart")
    public String createdevices(Model model) {
        return "chart"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
