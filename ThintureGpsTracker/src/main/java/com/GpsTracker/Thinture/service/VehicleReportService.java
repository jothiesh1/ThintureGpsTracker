package com.GpsTracker.Thinture.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.dto.VehicleReportDTO;
import com.GpsTracker.Thinture.repository.VehicleReportRepository;
@Service
public class VehicleReportService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleReportService.class);

    @Autowired
    private VehicleReportRepository vehicleReportRepository;

    public List<VehicleReportDTO> getVehicleReport(String deviceID, Timestamp startDate, Timestamp endDate) {
        logger.info("Fetching vehicle report for DeviceID: {}, StartDate: {}, EndDate: {}", deviceID, startDate, endDate);
        
        List<Object[]> result = vehicleReportRepository.findVehicleReport(deviceID, startDate, endDate);
        
        if (result.isEmpty()) {
            logger.warn("No data found for DeviceID: {}", deviceID);
        }

        List<VehicleReportDTO> reportList = new ArrayList<>();
        
        for (Object[] row : result) {
            VehicleReportDTO report = new VehicleReportDTO();
            logger.debug("Mapping result row: {}", Arrays.toString(row));

            report.setDeviceID((String) row[0]);
            report.setVehicleNumber((String) row[1]);
            report.setVehicleType((String) row[2]);
            report.setOwnerName((String) row[3]);
            report.setEngineNumber((String) row[4]);
            report.setManufacturer((String) row[5]);
            report.setModel((String) row[6]);
            report.setInstallationDate((Date) row[7]);
            report.setSerialNo((String) row[8]);
            report.setTechnicianName((String) row[9]);
            report.setImei((String) row[10]);
            report.setSimNumber((String) row[11]);
            report.setDealerName((String) row[12]);
            report.setAddressPhone((String) row[13]);
            report.setCountry((String) row[14]);
            report.setLatitude((Double) row[15]);
            report.setLongitude((Double) row[16]);
            report.setTimestamp((Timestamp) row[17]);
            report.setVehicleSpeed((Double) row[18]);
            report.setCourse((String) row[19]);
            report.setAdditionalData((String) row[20]);
            report.setSequenceNumber((Integer) row[21]);
            report.setViolationDate((Date) row[22]);
            report.setViolationType((String) row[23]);
            report.setViolationSpeed((String) row[24]);
            report.setLocation((String) row[25]);
            report.setStatus((String) row[26]);

            reportList.add(report);
        }

        logger.info("Returning {} records for DeviceID: {}", reportList.size(), deviceID);
        return reportList;
    }
}
