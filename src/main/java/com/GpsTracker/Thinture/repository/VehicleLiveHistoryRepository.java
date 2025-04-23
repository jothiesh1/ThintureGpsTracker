package com.GpsTracker.Thinture.repository;


import com.GpsTracker.Thinture.model.VehicleLastLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleLiveHistoryRepository extends JpaRepository<VehicleLastLocation, Long> {
    
    /**
     * Find vehicle last location by device ID
     */
    Optional<VehicleLastLocation> findByDeviceId(String deviceId);
    
    /**
     * Custom query to get all live locations with a specific field ordering
     */
    @Query("SELECT v FROM VehicleLastLocation v ORDER BY v.timestamp DESC")
    List<VehicleLastLocation> findAllOrderByTimestampDesc();
    
    /**
     * Find all vehicle last locations
     * This method should work without a custom query, but we define it explicitly
     * to ensure compatibility
     */
    @Override
    @Query("SELECT v FROM VehicleLastLocation v")
    List<VehicleLastLocation> findAll();
    
    /**
     * Native query as a fallback if JPQL queries have issues
     */
    @Query(value = "SELECT * FROM vehicle_last_location", nativeQuery = true)
    List<VehicleLastLocation> findAllNative();

    
    /**
     * Custom query to get all live locations ordered by timestamp
     */
    @Query("SELECT v FROM VehicleLastLocation v ORDER BY v.timestamp DESC")
    List<VehicleLastLocation> findAllLiveLocations();  // ✅ FIXED METHOD NAME
}