package com.GpsTracker.Thinture.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleLastLocation;

import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

import java.util.List;
import java.util.Optional;

/** ࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏
 * Author: Jothiesh ☃☃☃☃☃☃☃
 * Senior Developer, R&D 
 *
 * ⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂
 * 
 */


@Service
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    
    private final VehicleRepository vehicleRepository;
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;

//    // Save or update a vehicle
//    public Vehicle save(Vehicle vehicle) {
//        return vehicleRepository.save(vehicle);
//    }

    // Retrieve all vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // Retrieve a vehicle by ID
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    // Count all vehicles
    public long countAllVehicles() {
        return vehicleRepository.count();
    }

    // Find a vehicle by device ID
    public Optional<Vehicle> getVehicleByDeviceID(String deviceID) {
        return vehicleRepository.findByDeviceID(deviceID);
    }

    // Retrieve the last known locations of all vehicles
    public List<VehicleLastLocation> getLastKnownLocations() {
        return vehicleLastLocationRepository.findAll();
    }

    
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    
    public List<VehicleLastLocation> getAllLastKnownLocations() {
        return vehicleLastLocationRepository.findAll();
    }

    public Optional<VehicleLastLocation> getLastKnownLocationByDeviceId(String deviceId) {
        return vehicleLastLocationRepository.findByDeviceId(deviceId);
    }

//    public void save(Vehicle vehicle) {
//        logger.info("Attempting to save vehicle: {}", vehicle);
//
//        // Ensure that the deviceID is null if it is an empty string
//        if (vehicle.getDeviceID() != null && vehicle.getDeviceID().isEmpty()) {
//            vehicle.setDeviceID(null);
//        }
//
//        try {
//            vehicleRepository.save(vehicle);
//            logger.info("Vehicle saved successfully: {}", vehicle);
//        } catch (Exception e) {
//            logger.error("Error occurred while saving vehicle: {}", e.getMessage());
//            throw e;
//        }
//    }

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        logger.info("Finding vehicle by number: {}", vehicleNumber);
        return vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElse(null);
    }

    public void save(Vehicle vehicle) {
        logger.info("Saving vehicle: {}", vehicle);
        vehicleRepository.save(vehicle);
        logger.info("Vehicle saved successfully: {}", vehicle);
    }
    
    public Vehicle addDeviceInformation(Date installationDate, String deviceID, String technicianName,
            String imei, String simNumber, String dealerName,
            String addressPhone, String country) {
logger.info("Adding new device information.");
Vehicle vehicle = new Vehicle();
vehicle.setInstallationDate(installationDate);
vehicle.setDeviceID(deviceID);
vehicle.setTechnicianName(technicianName);
vehicle.setImei(imei);
vehicle.setSimNumber(simNumber);
vehicle.setDealerName(dealerName);
vehicle.setAddressPhone(addressPhone);
vehicle.setCountry(country);

logger.info("Device information added successfully.");
return vehicleRepository.save(vehicle);
}
}