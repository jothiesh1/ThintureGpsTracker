package com.GpsTracker.Thinture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.model.vehicle;
//import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.VehicleService;

@Controller
@RequestMapping("/total_vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Method to render the view with vehicle data
    @GetMapping
    public String getAllVehicles(Model model) {
        List<vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("total_vehicles", vehicles);
        return "total_vehicles"; // Ensure this matches the template name
    }
}