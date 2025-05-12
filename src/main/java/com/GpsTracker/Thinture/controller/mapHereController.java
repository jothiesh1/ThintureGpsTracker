package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class mapHereController {
	 @GetMapping("/mapHere")
	    public String mapView() {
	        return "mapHere";
	    }
}
