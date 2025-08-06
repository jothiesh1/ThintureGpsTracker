package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.model.Driver;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Optional: Filter drivers by roles
    List<Driver> findByAdminId(Long adminId);
    List<Driver> findByDealerId(Long dealerId);
    List<Driver> findByUserId(Long userId);
    List<Driver> findBySuperAdminId(Long superAdminId);
    List<Driver> findByClientId(Long clientId);

    // Optional: Find by RFID
    Driver findByRfid(String rfid);
}
