package com.GpsTracker.Thinture.controller;

import java.util.List;

//import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/total_vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String getVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "total_vehicles";
    }

    @PostMapping("/add")
    public String addVehicle(Vehicle vehicle) {
        vehicleService.addVehicle(vehicle);
        return "redirect:/total_vehicles";
    }


    

    
    // code for the visible map in the javascript page
    @GetMapping("/locations")
    public List<Vehicle> getGpsLocations() {
        return vehicleService.getAllVehicles(); // Fetches all vehicles from the database
    }
//    @DeleteMapping("/delete/{deviceID}")
//    public ResponseEntity<Void> deleteVehicle(@PathVariable String deviceID) {
//        vehicleService.deleteVehicle(deviceID);
//        return ResponseEntity.noContent().build();
//    }
    
}
