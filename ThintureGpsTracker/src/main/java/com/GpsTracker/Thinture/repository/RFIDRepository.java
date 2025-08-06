package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.RFID;

@Repository
public interface RFIDRepository extends JpaRepository<RFID, Long> {
    boolean existsByRfidCode(String rfidCode);
    
    @Query("SELECT COUNT(r) FROM RFID r WHERE r.client.id = :clientId")
    long countByClientId(@Param("clientId") Long clientId);
}
