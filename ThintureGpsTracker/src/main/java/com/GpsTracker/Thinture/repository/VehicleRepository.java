package com.GpsTracker.Thinture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GpsTracker.Thinture.model.vehicle;



public interface VehicleRepository extends JpaRepository<vehicle, Long> {

	void deleteByDeviceID(String deviceID);

	
    // You can add custom query methods if needed
}
//