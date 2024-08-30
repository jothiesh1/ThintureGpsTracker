package com.GpsTracker.Thinture.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;

@Service
public class VehicleLastLocationService {

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;

    public List<VehicleLastLocation> getAllLastKnownLocations() {
        return vehicleLastLocationRepository.findAll();
    }
    public long countAllVehicles() {
        return vehicleLastLocationRepository.count();
    }

    public long countVehiclesByStatus(String status) {
        return vehicleLastLocationRepository.countByStatus(status);
    }
}
