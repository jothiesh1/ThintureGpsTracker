package com.GpsTracker.Thinture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.GpsData;

@Repository
public interface GpsDataRepository extends JpaRepository<GpsData, Long> {
    Optional<GpsData> findByDeviceID(String deviceID);
    void deleteByDeviceID(String deviceID);
    // Find the latest GPS data by deviceID
    @Query("SELECT g FROM GpsData g WHERE g.deviceID = :deviceID ORDER BY g.timestamp DESC")
    GpsData findLatestByDeviceID(@Param("deviceID") String deviceID);
    @Query("SELECT g FROM GpsData g WHERE g.id IN (SELECT MAX(id) FROM GpsData GROUP BY deviceID)")
    List<GpsData> findAllLatestGpsData();
}
//