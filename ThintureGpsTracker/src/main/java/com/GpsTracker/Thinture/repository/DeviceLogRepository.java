package com.GpsTracker.Thinture.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.VehicleHistory;

@Repository
public interface DeviceLogRepository extends JpaRepository<VehicleHistory, Long> {

	@Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceID AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
	List<VehicleHistory> findHistoryByDeviceIDAndDateRange(
	    @Param("deviceID") String deviceID,
	    @Param("startDate") Timestamp startDate,
	    @Param("endDate") Timestamp endDate
	);

}
