package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LiveVehicleService {

    private static final Logger logger = LoggerFactory.getLogger(LiveVehicleService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    public Optional<Vehicle> getVehicleByDeviceID(String deviceId) {
        logger.debug("\u001B[34m[LiveVehicle] Looking up vehicle with deviceID: {}\u001B[0m", deviceId);
        return vehicleRepository.findByDeviceID(deviceId);
    }
    
    public Optional<Vehicle> getVehicleById(Long id) {
        logger.debug("\u001B[34m[LiveVehicle] Looking up vehicle with ID: {}\u001B[0m", id);
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> getAllVehicles() {
        logger.debug("\u001B[34m[LiveVehicle] Fetching all vehicles\u001B[0m");
        return vehicleRepository.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        logger.info("\u001B[32m[LiveVehicle] Saving vehicle: {}\u001B[0m", vehicle.getDeviceID());
        return vehicleRepository.save(vehicle);
    }
}