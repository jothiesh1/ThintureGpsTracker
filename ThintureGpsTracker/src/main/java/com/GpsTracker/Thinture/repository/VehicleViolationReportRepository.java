package com.GpsTracker.Thinture.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.dto.VehicleViolationReportDTO;
import com.GpsTracker.Thinture.model.VehicleHistory;

@Repository

	public interface VehicleViolationReportRepository extends JpaRepository<VehicleHistory, Long> {

	@Query(value = "SELECT v.deviceID, v.vehicleNumber, v.vehicleType, v.dealerName, v.ownerName, " +
            "vh.additionalData, vh.timestamp, vh.latitude, vh.longitude, vh.speed, vh.status " +
            "FROM vehicle_history vh " +
            "LEFT JOIN vehicle v ON v.deviceID = vh.device_id " +
            "WHERE (:deviceId IS NULL OR vh.device_id = :deviceId) " +
            "AND (:startDate IS NULL OR vh.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR vh.timestamp <= :endDate) " +
            "AND (:additionalData IS NULL OR vh.additionalData LIKE CONCAT('%', :additionalData, '%')) " +
            "ORDER BY vh.timestamp DESC",
    nativeQuery = true)
	    List<Object[]> findDetailedVehicleReports(@Param("deviceId") String deviceId,
	                                              @Param("startDate") Timestamp startDate,
	                                              @Param("endDate") Timestamp endDate,
	                                              @Param("additionalData") String additionalData);
	    
	    
	    
	    
	    
	    
	    //DAHSHBOARD CODE CHART
	    
	    
	    @Query(value = "SELECT " +
	            "SUM(CASE WHEN additionalData LIKE '%Over speed%' THEN 1 ELSE 0 END) as overspeed, " +
	            "SUM(CASE WHEN additionalData LIKE '%Sharp Turning%' THEN 1 ELSE 0 END) as sharpTurning, " +
	            "SUM(CASE WHEN additionalData LIKE '%Harsh Acceleration%' THEN 1 ELSE 0 END) as harshAcceleration, " +
	            "SUM(CASE WHEN additionalData LIKE '%Harsh Breaking%' THEN 1 ELSE 0 END) as harshBreaking " +
	            "FROM vehicle_history " +
	            "WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY)",
	            nativeQuery = true)
	    List<Object[]> getViolationSummaryLast30Days();
	
	}


