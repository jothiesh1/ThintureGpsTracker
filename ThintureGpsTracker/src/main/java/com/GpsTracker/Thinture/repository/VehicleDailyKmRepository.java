package com.GpsTracker.Thinture.repository;


import com.GpsTracker.Thinture.model.VehicleDailyKm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDailyKmRepository extends JpaRepository<VehicleDailyKm, Long> {

    // 🔁 Prevent duplicate records
    boolean existsByDeviceIdAndDateAndTimeSlot(String deviceId, LocalDate date, String timeSlot);

    // 📥 Fetch all slots for a given day/device
    List<VehicleDailyKm> findByDeviceIdAndDateOrderByTimeSlotAsc(String deviceId, LocalDate date);

    // 📅 Fetch all records for a given date
    List<VehicleDailyKm> findByDate(LocalDate date);
}
