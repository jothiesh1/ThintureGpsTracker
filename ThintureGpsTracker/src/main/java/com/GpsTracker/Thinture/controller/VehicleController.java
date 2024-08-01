package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.VehicleService;
import java.util.*;
@Controller
@RequestMapping("/total_vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String getAllVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "total_vehicles"; // Ensure this matches the template name
    }

    @PostMapping("/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle) {
        System.out.println("Received Vehicle: " + vehicle); // Add debug print
        vehicleService.saveVehicle(vehicle);
        return "redirect:/total_vehicles";
    }
}
