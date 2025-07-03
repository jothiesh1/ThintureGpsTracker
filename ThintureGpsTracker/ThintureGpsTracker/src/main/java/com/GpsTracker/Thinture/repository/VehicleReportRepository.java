package com.GpsTracker.Thinture.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.Vehicle;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;


//AOP Filter 
//public interface VehicleReportRepository extends JpaRepository<Vehicle, Long> {
@Repository
public interface VehicleReportRepository extends BaseRestrictedRepository<Vehicle, Long> {
 



    /**
     * Fetch filtered reports from `vehicle_history`.
     *
     * @param startDate Filter by start timestamp (nullable).
     * @param endDate   Filter by end timestamp (nullable).
     * @param deviceId  Filter by device ID (optional).
     * @return List of Object arrays containing report data.
     */
	 /**
     * Fetch filtered reports from `vehicle_history`.
     * This query retrieves data from the `vehicle_history` table based on optional filters.
     *
     * @param startDate Filter by start timestamp (nullable).
     * @param endDate   Filter by end timestamp (nullable).
     * @param deviceId  Filter by device ID (optional).Z
	 * @param vehicleStatus 
     * @return List of Object arrays containing report data.
     */
	@Query(value = "SELECT device_id, latitude, longitude, speed, timestamp, ignition, vehicleStatus " +
            "FROM vehicle_history " +
            "WHERE (:start IS NULL OR timestamp >= :start) " +
            "AND (:end IS NULL OR timestamp <= :end) " +
            "AND (:deviceId IS NULL OR device_id = :deviceId) " +
            "AND (:vehicleStatus IS NULL OR vehicleStatus = :vehicleStatus) " +
            "ORDER BY timestamp DESC",
    nativeQuery = true)
List<Object[]> findReports(@Param("start") Timestamp startDate,
                         @Param("end") Timestamp endDate,
                         @Param("deviceId") String deviceId,
                         @Param("vehicleStatus") String vehicleStatus);



    /**
     * Fetch detailed vehicle reports by joining `Vehicle`, `VehicleHistory`, and `Device` entities.
     * This query retrieves enriched vehicle report data with optional filters.
     *
     * @param deviceID  Filter by device ID (optional).
     * @param startDate Filter by start timestamp (nullable).
     * @param endDate   Filter by end timestamp (nullable).
     * @return List of Object arrays containing detailed report data.
     */
    @Query("SELECT DISTINCT v.deviceID, v.vehicleNumber, v.vehicleType, v.ownerName, v.engineNumber, " +
           "v.manufacturer, v.model, d.installationDate, d.SerialNo, d.technicianName, d.simNumber, " +
           "d.dealerName, d.addressPhone, d.country, vh.latitude, vh.longitude, vh.timestamp, vh.speed, " +
           "vh.course, vh.additionalData, vh.sequenceNumber, vh.ignition, vh.vehicleStatus " +
           "FROM Vehicle v " +
           "LEFT JOIN VehicleHistory vh ON v.deviceID = vh.vehicle.deviceID " +
           "JOIN Device d ON v.deviceID = d.SerialNo " +
           "WHERE (:deviceID IS NULL OR v.deviceID = :deviceID) " +
           "AND (:startDate IS NULL OR vh.timestamp >= :startDate) " +
           "AND (:endDate IS NULL OR vh.timestamp <= :endDate) " +
           "ORDER BY vh.timestamp DESC")
    List<Object[]> findVehicleReport(@Param("deviceID") String deviceID,
                                     @Param("startDate") Timestamp startDate,
                                     @Param("endDate") Timestamp endDate);

    /**
     * Fetch reports from the vehicle history table with status 'parked'.
     *
     * @param startDate Filter by start timestamp (nullable).
     * @param endDate   Filter by end timestamp (nullable).
     * @return List of Object arrays containing parked data.
     */
    @Query(value = "SELECT device_id, latitude, longitude, speed, timestamp, vehicle_status, ignition " +
            "FROM vehicle_history " +
            "WHERE (:start IS NULL OR timestamp >= :start) " +
            "AND (:end IS NULL OR timestamp <= :end) " +
            "AND vehicle_status = 'parked' " +
            "ORDER BY device_id, timestamp", 
    nativeQuery = true)
    List<Object[]> findParkedReports(@Param("start") Timestamp startDate, @Param("end") Timestamp endDate);
    
    
    
    
    //parking code
    @Query(value = """
            SELECT 
                vh1.device_id AS deviceId,
                vh1.timestamp AS startParkedTime,
                vh1.latitude AS parkedLatitude,
                vh1.longitude AS parkedLongitude,
                MIN(vh2.timestamp) AS endParkedTime,
                TIMESTAMPDIFF(MINUTE, vh1.timestamp, MIN(vh2.timestamp)) AS parkedDurationMinutes,
                CONCAT(
                    TIMESTAMPDIFF(DAY, vh1.timestamp, MIN(vh2.timestamp)), ' days, ',
                    FLOOR((TIMESTAMPDIFF(MINUTE, vh1.timestamp, MIN(vh2.timestamp)) % 1440) / 60), ' hours, ',
                    (TIMESTAMPDIFF(MINUTE, vh1.timestamp, MIN(vh2.timestamp)) % 60), ' minutes'
                ) AS parkedDurationFull
            FROM 
                vehicle_history vh1
            LEFT JOIN 
                vehicle_history vh2
            ON 
                vh1.device_id = vh2.device_id
                AND vh2.timestamp > vh1.timestamp
                AND vh2.vehicleStatus IN ('ON', 'IDLE', 'RUNNING')
            WHERE 
                vh1.vehicleStatus = 'PARKED'
                AND (:fromDate IS NULL OR vh1.timestamp >= :fromDate)
                AND (:toDate IS NULL OR vh1.timestamp <= :toDate)
            GROUP BY 
                vh1.device_id, vh1.timestamp, vh1.latitude, vh1.longitude
            ORDER BY 
                vh1.device_id, vh1.timestamp
        """, nativeQuery = true)
    List<Object[]> findParkingDurations(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );
}

