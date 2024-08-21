package com.GpsTracker.Thinture.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.vehicle;
//import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;


import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public vehicle saveVehicle(vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    
    // Additional service methods if needed
    // ...
}
