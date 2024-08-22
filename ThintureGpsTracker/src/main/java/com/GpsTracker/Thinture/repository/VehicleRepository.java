package com.GpsTracker.Thinture.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GpsTracker.Thinture.model.vehicle;



public interface VehicleRepository extends JpaRepository<vehicle, Long> {

	void deleteByDeviceID(String deviceID);

	// long countByCreatedDateAfter(LocalDate firstDayOfMonth);
	Optional<vehicle> findByDeviceID(String deviceID);
	
    // You can add custom query methods if needed
}
//