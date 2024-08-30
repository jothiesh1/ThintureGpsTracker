package com.GpsTracker.Thinture.repository;
//package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {

//	@Query("SELECT vh FROM VehicleHistory vh " +
//	           "JOIN (SELECT vh2.vehicle.id as vehicleId, MAX(vh2.timestamp) as maxTimestamp " +
//	           "FROM VehicleHistory vh2 GROUP BY vh2.vehicle.id) latest " +
//	           "ON vh.vehicle.id = latest.vehicleId AND vh.timestamp = latest.maxTimestamp")
//	    List<VehicleHistory> findLastKnownLocations();
//	
}

