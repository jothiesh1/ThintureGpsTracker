package com.GpsTracker.Thinture.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.Vehicle;

import java.security.Timestamp;
import java.util.List;

@Repository
public interface VehicleReportRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT DISTINCT v.deviceID, v.vehicleNumber, v.vehicleType, v.ownerName, v.engineNumber, " +
           "v.manufacturer, v.model, d.installationDate, d.SerialNo, d.technicianName, d.imei, d.simNumber, " +
           "d.dealerName, d.addressPhone, d.country, vh.latitude, vh.longitude, vh.timestamp, vh.speed AS vehicle_speed, " +
           "vh.course, vh.additionalData, vh.sequenceNumber, vr.violationDate, vr.violationType, vr.speed AS violation_speed, " +
           "vr.location, vr.status " +
           "FROM Vehicle v " +
           "LEFT JOIN VehicleHistory vh ON v.deviceID = vh.vehicle.deviceID " +
           "LEFT JOIN ViolationReport vr ON vh.id = vr.vehicleHistory.id " +
           "JOIN Device d ON v.deviceID = d.SerialNo " +
           "WHERE v.deviceID = :deviceID " +
           "AND vh.timestamp BETWEEN :startDate AND :endDate")
    List<Object[]> findVehicleReport(@Param("deviceID") String deviceID,
                                     @Param("startDate") java.sql.Timestamp startDate,
                                     @Param("endDate") java.sql.Timestamp endDate);
}
