package com.GpsTracker.Thinture.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
@Repository
public interface VehicleLastLocationRepository extends JpaRepository<VehicleLastLocation, Long> {
	// Method to fetch all records
    List<VehicleLastLocation> findAll();

    // Method to find by device ID
    Optional<VehicleLastLocation> findByDeviceId(String deviceId);

    // Count by status
    long countByStatus(String status);
    
}
