package com.GpsTracker.Thinture.repository;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.GpsTracker.Thinture.model.ViolationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Repository
public interface ViolationReportRepository extends JpaRepository<ViolationReport, Long> {

    // Query to fetch violation reports based on deviceID and a date range
    List<ViolationReport> findByVehicle_DeviceIDAndViolationDateBetween(String deviceID, Timestamp startDate, Timestamp endDate);

    // Query to fetch violation reports based on deviceID
    List<ViolationReport> findByVehicle_DeviceID(String deviceID);
}
