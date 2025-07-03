package com.GpsTracker.Thinture.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.VehicleHistory;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DriverRagReportRepository extends JpaRepository<VehicleHistory, Long> {

    @Query(value = """
        SELECT 
            v.deviceID,
            v.vehicleNumber,
            v.vehicleType,
            v.dealerName,
            v.ownerName,
            vh.additionalData,
            vh.timestamp,
            vh.latitude,
            vh.longitude,
            vh.speed,
            vh.status,
            d.id AS driver_id,
            r.rfid_code
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        LEFT JOIN 
            drivers d ON vh.driver_id = d.id
        LEFT JOIN 
            rfid r ON r.client_id = v.client_id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
            AND vh.device_id = :deviceId
        ORDER BY 
            vh.timestamp DESC
        """, nativeQuery = true)
    List<Object[]> getDriverRagReportRaw(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end,
        @Param("deviceId") String deviceId
    );

    @Query("SELECT DISTINCT v.deviceID FROM Vehicle v")
    List<String> findAllDeviceIds();
}