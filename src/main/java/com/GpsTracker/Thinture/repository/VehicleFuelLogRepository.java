package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.VehicleFuelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface VehicleFuelLogRepository extends JpaRepository<VehicleFuelLog, Long> {

    @Query("SELECT SUM(f.fuelFilled) FROM VehicleFuelLog f WHERE f.deviceId = :deviceId AND f.date = :date")
    Double getTotalFuelFilled(@Param("deviceId") String deviceId, @Param("date") LocalDate date);
}
