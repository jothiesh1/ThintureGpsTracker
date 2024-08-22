package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
//
//    public Vehicle save(Vehicle vehicle) {
//        return vehicleRepository.save(vehicle);
//    }
//
    public List<vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }
//
//    public void deleteVehicle(Long id) {
//        vehicleRepository.deleteById(id);
//    }
    public void deleteVehicle(String deviceID) {
        vehicleRepository.deleteByDeviceID(deviceID);
    }

	public vehicle save(vehicle vehicle) {
		 return vehicleRepository.save(vehicle);// TODO Auto-generated method stub
		
	}

}