package com.GpsTracker.Thinture.repository;
import java.util.List;
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

    Optional<Vehicle> findByDeviceID(String deviceID);

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

    Optional<Vehicle> findBySerialNo(String serialNo);
    
    
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

}
  

