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

    static final Logger logger = LoggerFactory.getLogger(ViolationReportRepository.class);

    // Query to fetch violation reports based on deviceID and a date range
    List<ViolationReport> findByVehicle_DeviceIDAndViolationDateBetween(String deviceID, Timestamp startDate, Timestamp endDate);

    default List<ViolationReport> logFindByVehicleDeviceID(String deviceID) {
        logger.info("Querying violation reports for deviceId: {}", deviceID);
        try {
            List<ViolationReport> results = findByVehicle_DeviceID(deviceID);
            logger.info("Query successful, found {} violation reports for deviceId: {}", results.size(), deviceID);
            return results;
        } catch (Exception e) {
            logger.error("Error querying violation reports for deviceId: {}", deviceID, e);
            throw e;
        }
    }

    List<ViolationReport> findByVehicle_DeviceID(String deviceID);
   // Query to fetch violation reports based on deviceID and date range
   // List<ViolationReport> findByVehicle_DeviceIDAndViolationDateBetween(String deviceID, Timestamp startDate, Timestamp endDate);
}
