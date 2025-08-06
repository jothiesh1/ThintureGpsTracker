package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface DistanceKmCalcRepository extends JpaRepository<VehicleHistory, Long> {

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findByDeviceIdOrdered(@Param("deviceId") String deviceId);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.imei = :imei ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findByImeiOrdered(@Param("imei") String imei);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND DATE(vh.timestamp) = CURDATE() ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findTodayByDeviceId(@Param("deviceId") String deviceId);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.imei = :imei AND DATE(vh.timestamp) = CURDATE() ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findTodayByImei(@Param("imei") String imei);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findByDeviceIdAndDateRange(
            @Param("deviceId") String deviceId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.imei = :imei AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findByImeiAndDateRange(
            @Param("imei") String imei,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.timestamp = (SELECT MAX(vh2.timestamp) FROM VehicleHistory vh2 WHERE vh2.vehicle.deviceID = vh.vehicle.deviceID)")
    List<VehicleHistory> findAllLatestLocations();

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND vh.panic = 1 AND DATE(vh.timestamp) = CURDATE()")
    List<VehicleHistory> findTodayPanicLogs(@Param("deviceId") String deviceId);

    VehicleHistory findTopByVehicle_DeviceIDOrderByTimestampDesc(String deviceId);

    @Query("SELECT COUNT(vh) FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND vh.timestamp BETWEEN :start AND :end")
    long countLogsInRange(@Param("deviceId") String deviceId, @Param("start") Timestamp start, @Param("end") Timestamp end);

    // ✅ Mileage-related
   
}
