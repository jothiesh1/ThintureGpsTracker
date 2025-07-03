package com.GpsTracker.Thinture.controller;

//package com.ThintureGpsTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/map")
    public String showMap(Model model) {
        // Add any attributes to the model if necessary
        return "map"; // Ensure this matches the HTML file in your templates folder, e.g., map.html
    }
    
   

}

//
