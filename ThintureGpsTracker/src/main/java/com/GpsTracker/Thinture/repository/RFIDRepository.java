package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.RFID;

@Repository
public interface RFIDRepository extends JpaRepository<RFID, Long> {
    boolean existsByRfidCode(String rfidCode);
}
