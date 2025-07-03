package com.GpsTracker.Thinture.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.dto.VehicleViolationReportDTO;
import com.GpsTracker.Thinture.model.VehicleHistory;

//AOP filter 

//	public interface VehicleViolationReportRepository extends JpaRepository<VehicleHistory, Long> {

public interface VehicleViolationReportRepository extends BaseRestrictedRepository <VehicleHistory, Long> {

	    @Query(value = "SELECT v.deviceID, v.vehicleNumber, v.vehicleType, v.dealerName, v.ownerName, " +
	                   "vh.additionalData, vh.timestamp, vh.latitude, vh.longitude, vh.speed, vh.status " +
	                   "FROM ThintureGpsTrackerDB2.vehicle v " +
	                   "INNER JOIN ThintureGpsTrackerDB2.vehicle_history vh ON v.deviceID = vh.device_id " +
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
	}


