package com.GpsTracker.Thinture.repository;
//package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.VehicleHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import com.GpsTracker.Thinture.model.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {
    
    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceID AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findHistoryByDeviceIDAndDateRange(
            @Param("deviceID") String deviceID,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceID ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findHistoryByDeviceID(@Param("deviceID") String deviceID);

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID IN :deviceIDs AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findHistoryByMultipleDeviceIDsAndDateRange(
            @Param("deviceIDs") List<String> deviceIDs,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

    VehicleHistory findTopByVehicle_DeviceIDOrderByTimestampDesc(String deviceID);
    
  

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :vehicleId AND YEAR(vh.timestamp) = :year")
    List<VehicleHistory> findByVehicleIdAndYear(
        @Param("vehicleId") String vehicleId, 
        @Param("year") int year
    );

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :vehicleId")
    List<VehicleHistory> findByVehicleId(@Param("vehicleId") String vehicleId);

    // New query for vehicle violations and vehicle report
    @Query("SELECT vh FROM VehicleHistory vh LEFT JOIN FETCH vh.violationReports WHERE vh.vehicle.deviceID = :deviceID")
    List<VehicleHistory> findByDeviceIDWithViolations(@Param("deviceID") String deviceID);

 // Query for fetching based on device ID, month, and year
   // @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND MONTH(vh.timestamp) = :month AND YEAR(vh.timestamp) = :year")
    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceId AND MONTH(vh.timestamp) = :month AND YEAR(vh.timestamp) = :year")
    List<VehicleHistory> findByDeviceIDMonthYear(
        @Param("deviceId") String deviceId, 
        @Param("month") int month, 
        @Param("year") int year
    );
//	List<VehicleHistory> findByVehicleIdAndMonthAndYear(String deviceID, int month, int year);
    @Query(value = "SELECT DISTINCT v.deviceID, v.vehicleNumber, v.vehicleType, v.ownerName, " +
            "vh.latitude, vh.longitude, vh.timestamp, v.engineNumber, v.manufacturer, v.model, " +
            "v.technicianName, v.imei, v.simNumber, v.dealerName, v.addressPhone, v.country, " +
            "vr.violation_type, vr.violation_date, vr.location, vr.speed, vr.status " +
            "FROM vehicle v " +
            "LEFT JOIN vehicle_history vh ON v.deviceID = vh.device_id " +
            "LEFT JOIN violation_report vr ON vh.id = vr.vehicle_history_id " +
            "WHERE v.deviceID = :deviceID " +
            "AND vh.timestamp BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Object[]> findVehicleReportByDeviceIdAndDateRange(@Param("deviceID") String deviceID,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate);
}






/*
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {

    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle.deviceID = :deviceID AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp ASC")
    List<VehicleHistory> findHistoryByDeviceIDAndDateRange(@Param("deviceID") String deviceID,
                                                          @Param("startDate") Timestamp startDate,
                                                          @Param("endDate") Timestamp endDate);

*/




//@Query("SELECT vh FROM VehicleHistory vh " +
//           "JOIN (SELECT vh2.vehicle.id as vehicleId, MAX(vh2.timestamp) as maxTimestamp " +
//           "FROM VehicleHistory vh2 GROUP BY vh2.vehicle.id) latest " +
//           "ON vh.vehicle.id = latest.vehicleId AND vh.timestamp = latest.maxTimestamp")
//    List<VehicleHistory> findLastKnownLocations();


//
        // below for vehicle replay button code fetching data from device	


//List<VehicleHistory> findByDeviceIdAndTimestampBetween(String deviceID, Timestamp startDate, Timestamp endDate);

