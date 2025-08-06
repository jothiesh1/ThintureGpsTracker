package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.GpsTracker.Thinture.model.VehicleHistory;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DriverRagReportRepository extends JpaRepository<VehicleHistory, Long> {

    /**
     * Get Driver RAG Report data by Driver ID (REAL DATABASE QUERY)
     */
    @Query(value = """
        SELECT 
            v.deviceID as device_id,
            v.vehicleNumber as vehicle_number,
            v.vehicleType as vehicle_type,
            v.dealerName as dealer_name,
            v.ownerName as owner_name,
            v.serial_no as vehicle_serial,
            vh.timestamp,
            vh.latitude,
            vh.longitude,
            vh.speed,
            vh.status,
            vh.additionalData as additional_data,
            d.id as driver_id,
            d.fullName as driver_name,
            d.email as driver_email,
            d.rfid as driver_rfid,
            vh.course,
            vh.ignition,
            vh.vehicleStatus as vehicle_status
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        LEFT JOIN 
            newdrivers d ON v.driver_id = d.id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
            AND (:driverId IS NULL OR d.id = :driverId)
        ORDER BY 
            d.fullName, vh.timestamp DESC
        """, nativeQuery = true)
    List<Object[]> getDriverRagReportByDriverId(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end,
        @Param("driverId") Long driverId
    );

    /**
     * Get Driver RAG Report data by Device ID (FIXED - no v.rfid)
     */
    @Query(value = """
        SELECT 
            v.deviceID as device_id,
            v.vehicleNumber as vehicle_number,
            v.vehicleType as vehicle_type,
            v.dealerName as dealer_name,
            v.ownerName as owner_name,
            v.serial_no as vehicle_serial,
            vh.timestamp,
            vh.latitude,
            vh.longitude,
            vh.speed,
            vh.status,
            vh.additionalData as additional_data,
            d.id as driver_id,
            d.fullName as driver_name,
            d.email as driver_email,
            d.rfid as driver_rfid,
            vh.course,
            vh.ignition,
            vh.vehicleStatus as vehicle_status
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        LEFT JOIN 
            newdrivers d ON v.driver_id = d.id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
            AND (:deviceId IS NULL OR v.deviceID = :deviceId)
        ORDER BY 
            v.deviceID, vh.timestamp DESC
        """, nativeQuery = true)
    List<Object[]> getDriverRagReportRaw(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end,
        @Param("deviceId") String deviceId
    );

    /**
     * Get all drivers with their devices (REAL DATABASE QUERY)
     */
 
    /**
     * Get all device IDs with driver information (FIXED)
     */
    @Query(value = """
        SELECT DISTINCT 
            v.deviceID,
            d.fullName as driver_name,
            d.email as driver_email,
            v.vehicleNumber,
            v.vehicleType
        FROM 
            vehicle v
        LEFT JOIN 
            newdrivers d ON v.driver_id = d.id
        WHERE 
            v.deviceID IS NOT NULL
        ORDER BY 
            d.fullName, v.deviceID
        """, nativeQuery = true)
    List<Object[]> findAllDeviceIdsWithDrivers();

    /**
     * Get distinct device IDs only
     */
    @Query("SELECT DISTINCT v.deviceID FROM Vehicle v WHERE v.deviceID IS NOT NULL")
    List<String> findAllDeviceIds();

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Get RAG performance summary for a specific driver by Driver ID
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_records,
            MAX(vh.speed) as max_speed,
            AVG(vh.speed) as avg_speed,
            COUNT(CASE WHEN vh.additionalData LIKE '%Over speed%' THEN 1 END) as speeding_violations,
            COUNT(CASE WHEN vh.additionalData LIKE '%Harsh Acceleration%' THEN 1 END) as harsh_accel_count,
            COUNT(CASE WHEN vh.additionalData LIKE '%Harsh Breaking%' THEN 1 END) as harsh_brake_count,
            COUNT(CASE WHEN vh.additionalData LIKE '%Sharp Turning%' THEN 1 END) as sharp_turn_count
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        INNER JOIN 
            newdrivers d ON v.driver_id = d.id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
            AND d.id = :driverId
        """, nativeQuery = true)
    Object[] getPerformanceSummaryByDriverId(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end,
        @Param("driverId") Long driverId
    );

    /**
     * Get RAG performance summary for a specific device (FIXED)
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_records,
            MAX(vh.speed) as max_speed,
            AVG(vh.speed) as avg_speed,
            COUNT(CASE WHEN vh.additionalData LIKE '%Over speed%' THEN 1 END) as speeding_violations,
            COUNT(CASE WHEN vh.additionalData LIKE '%Harsh Acceleration%' THEN 1 END) as harsh_accel_count,
            COUNT(CASE WHEN vh.additionalData LIKE '%Harsh Breaking%' THEN 1 END) as harsh_brake_count,
            COUNT(CASE WHEN vh.additionalData LIKE '%Sharp Turning%' THEN 1 END) as sharp_turn_count
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
            AND v.deviceID = :deviceId
        """, nativeQuery = true)
    Object[] getPerformanceSummary(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end,
        @Param("deviceId") String deviceId
    );

    /**
     * Get latest vehicle history records for all devices (for initial load)
     */
    @Query(value = """
        SELECT 
            v.deviceID as device_id,
            v.vehicleNumber as vehicle_number,
            v.vehicleType as vehicle_type,
            v.dealerName as dealer_name,
            v.ownerName as owner_name,
            v.serial_no as vehicle_serial,
            vh.timestamp,
            vh.latitude,
            vh.longitude,
            vh.speed,
            vh.status,
            vh.additionalData as additional_data,
            d.id as driver_id,
            d.fullName as driver_name,
            d.email as driver_email,
            d.rfid as driver_rfid,
            vh.course,
            vh.ignition,
            vh.vehicleStatus as vehicle_status
        FROM 
            vehicle v
        INNER JOIN 
            vehicle_history vh ON v.deviceID = vh.device_id
        LEFT JOIN 
            newdrivers d ON v.driver_id = d.id
        WHERE 
            vh.timestamp BETWEEN :startDate AND :endDate
        ORDER BY 
            v.deviceID, vh.timestamp DESC
        """, nativeQuery = true)
    List<Object[]> getAllDriverRagReportRaw(
        @Param("startDate") Timestamp start,
        @Param("endDate") Timestamp end
    );
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  /**
    * FIXED: Get all drivers with their devices - Use LEFT JOIN instead of INNER JOIN
    */
   @Query(value = """
       SELECT DISTINCT 
           d.id as driver_id,
           d.fullName as driver_name,
           d.email as driver_email,
           d.rfid as driver_rfid,
           COALESCE(v.deviceID, 'NO_DEVICE') as device_id,
           COALESCE(v.vehicleNumber, 'NO_VEHICLE') as vehicle_number,
           COALESCE(v.vehicleType, 'Unknown') as vehicle_type
       FROM 
           newdrivers d
       LEFT JOIN 
           vehicle v ON d.id = v.driver_id
       WHERE 
           d.fullName IS NOT NULL
           AND d.id IS NOT NULL
       ORDER BY 
           d.fullName
       """, nativeQuery = true)
   List<Object[]> findAllDriversWithDevices();

   /**
    * ALTERNATIVE: Even more lenient query if needed
    */
   @Query(value = """
       SELECT DISTINCT 
           d.id as driver_id,
           COALESCE(d.fullName, 'Unnamed Driver') as driver_name,
           d.email as driver_email,
           d.rfid as driver_rfid,
           COALESCE(v.deviceID, 'NO_DEVICE') as device_id,
           COALESCE(v.vehicleNumber, 'NO_VEHICLE') as vehicle_number,
           COALESCE(v.vehicleType, 'Unknown') as vehicle_type
       FROM 
           newdrivers d
       LEFT JOIN 
           vehicle v ON d.id = v.driver_id
       WHERE 
           d.id IS NOT NULL
       ORDER BY 
           d.fullName NULLS LAST
       """, nativeQuery = true)
   List<Object[]> findAllDriversWithDevicesLenient();
}