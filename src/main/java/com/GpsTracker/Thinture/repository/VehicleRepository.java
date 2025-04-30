package com.GpsTracker.Thinture.repository;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.GpsTracker.Thinture.model.Vehicle;

import jakarta.transaction.Transactional;
//AOP filter 
//public interface VehicleRepository extends JpaRepository <Vehicle, Long> {
public interface VehicleRepository extends BaseRestrictedRepository<Vehicle, Long> {

	@Transactional
    void deleteByDeviceID(String deviceID);

    

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    // Updated to match the camelCase field name in the entity
    //Optional<Vehicle> findBySerialNo(String serialNo);

    Optional<Vehicle> findByImei(String imei);

    Optional<Vehicle> findBySerialNoOrImei(String serialNo, String imei);
 // Find Serial Numbers by Prefix
   // @Query("SELECT v FROM Vehicle v WHERE v.serialNo = :serialNo")
    // Optional<Vehicle> findBySerialNo(@Param("serialNo") String serialNo);
    @Query("SELECT v.serialNo FROM Vehicle v WHERE v.serialNo LIKE CONCAT('%', :query, '%')")
    List<String> findSerialNosByQuery(@Param("query") String query);

 
    
 // Fetch all distinct device IDs from the vehicle table
    @Query("SELECT DISTINCT v.deviceID FROM Vehicle v")
    List<String> findAllDistinctDeviceIDs();
    
    
    @Query(value = "SELECT * FROM vehicle WHERE deviceID = :deviceID", nativeQuery = true)
    Optional<Vehicle> findVehicleByDeviceIDNative(@Param("deviceID") String deviceID);



    // ✅ Fetch All Serial Numbers (Avoid Duplicates)
    @Query("SELECT DISTINCT v.serialNo FROM Vehicle v WHERE v.serialNo IS NOT NULL")
    List<String> findAllSerialNumbers();
 
    
    @Query("SELECT v FROM Vehicle v WHERE v.serialNo LIKE CONCAT('%', :query, '%')")
    List<Vehicle> findVehiclesBySerialQuery(@Param("query") String query);

    
    
    
    
    
    @Query("SELECT v FROM Vehicle v WHERE (v.renewed = false OR v.renewed IS NULL) AND v.renewalDate IS NOT NULL")
    List<Vehicle> findUnrenewedVehiclesAll();


    @Query("SELECT v FROM Vehicle v WHERE (v.renewed = false OR v.renewed IS NULL) AND v.renewalDate < :date")
    List<Vehicle> findExpiredUnrenewed(@Param("date") Date date);


    @Query("SELECT v FROM Vehicle v WHERE v.renewalDate IS NOT NULL")
    List<Vehicle> findAllWithRenewalDateRegardlessOfRenewedStatus();


    @Query("SELECT v FROM Vehicle v WHERE (v.renewed = false OR v.renewed IS NULL) AND v.renewalDate BETWEEN :start AND :end")
    List<Vehicle> findUnrenewedVehiclesInRange(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT " +
    	       "SUM(CASE WHEN v.renewed = true THEN 1 ELSE 0 END) AS renewed, " +
    	       "SUM(CASE WHEN v.renewed = false THEN 1 ELSE 0 END) AS pending " +
    	       "FROM Vehicle v")
    	Map<String, Long> getRenewalStatusCounts();
    
   
    
    
    
    @Query(value = "SELECT DATE_FORMAT(installationDate, '%b-%Y') AS month, COUNT(*) AS installations FROM vehicle WHERE installationDate IS NOT NULL GROUP BY month", nativeQuery = true)
    List<Object[]> fetchInstallationsGroupedByMonth();

    
    @Query(value = "SELECT DATE_FORMAT(renewalDate, '%b-%Y') AS month, COUNT(*) AS renewals FROM vehicle WHERE renewalDate IS NOT NULL AND renewed = true GROUP BY month", nativeQuery = true)
    List<Object[]> fetchRenewalsGroupedByMonth();
    @Query("SELECT v.vehicleNumber FROM Vehicle v WHERE v.user_id = :userId")
    List<String> findVehicleNumbersByUserId(@Param("userId") Long userId);

    List<Vehicle> findBySerialNo(String serialNo);
    //  Optional<Vehicle> findBySerialNo(String serialNo);
    
    @Query("SELECT v FROM Vehicle v WHERE UPPER(v.deviceID) = UPPER(:deviceID)")
    Optional<Vehicle> findByDeviceID(@Param("deviceID") String deviceID);
    
    
    @Query("SELECT v.vehicleType, COUNT(v) " +
            "FROM Vehicle v " +
            "WHERE v.imei IS NOT NULL " +
            "AND v.deviceID IS NOT NULL " +
            "AND v.installationDate IS NOT NULL " +
            "GROUP BY v.vehicleType")
     List<Object[]> countByVehicleType();


}
  

