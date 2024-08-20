package com.GpsTracker.Thinture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GpsTracker.Thinture.model.Vehicle;



public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	void deleteByDeviceID(String deviceID);

	
    // You can add custom query methods if needed
}
//