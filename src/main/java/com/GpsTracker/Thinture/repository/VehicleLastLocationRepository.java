package com.GpsTracker.Thinture.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


//AOP Filter

//public interface VehicleLastLocationRepository extends JpaRepository<VehicleLastLocation, Long> {

@Repository
public interface VehicleLastLocationRepository extends BaseRestrictedRepository<VehicleLastLocation, Long> {
	 //code imie 
    
	  
	  Optional<VehicleLastLocation> findByImei(String imei);
	  Optional<VehicleLastLocation> findBySerialNo(String serialNo);

	  Optional<VehicleLastLocation> findByImeiAndSerialNo(String imei, String serialNo);

	  
	  
    /**
     * Fetch all records.
     * @return List of VehicleLastLocation.
     */
    List<VehicleLastLocation> findAll();

    /**
     * Find a record by device ID.
     * @param deviceId The device ID.
     * @return Optional containing the VehicleLastLocation if found.
     */
    Optional<VehicleLastLocation> findByDeviceId(String deviceId);

    /**
     * Count vehicles by status.
     * @param status The status to filter by.
     * @return Count of vehicles with the specified status.
     */
    long countByStatus(String status);

    /**
     * Find the latest location for a given device ID.
     * @param deviceId The device ID to search for.
     * @return The latest VehicleLastLocation for the specified device ID.
     */
    @Query("SELECT v FROM VehicleLastLocation v WHERE v.deviceId = :deviceId ORDER BY v.timestamp DESC")
    VehicleLastLocation findTopByDeviceIDOrderByTimestampDesc(@Param("deviceId") String deviceId);

    
   
}