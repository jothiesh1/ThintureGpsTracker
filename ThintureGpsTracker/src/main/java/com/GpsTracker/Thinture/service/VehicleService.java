package com.GpsTracker.Thinture.service;

import java.util.List;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // Additional service methods if needed
}