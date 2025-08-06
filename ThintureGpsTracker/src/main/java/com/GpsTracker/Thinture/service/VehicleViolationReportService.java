package com.GpsTracker.Thinture.service;


	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.dto.VehicleViolationReportDTO;
import com.GpsTracker.Thinture.dto.ViolationSummaryDTO;
import com.GpsTracker.Thinture.repository.VehicleViolationReportRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleViolationReportService {

    @Autowired
    private VehicleViolationReportRepository violationReportRepository;

    public List<VehicleViolationReportDTO> getViolationsByFilters(String deviceId, String additionalData,
                                                                  Timestamp startTimestamp, Timestamp endTimestamp) {

        List<Object[]> results = violationReportRepository.findDetailedVehicleReports(
                deviceId, startTimestamp, endTimestamp, additionalData
        );

        return results.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private VehicleViolationReportDTO mapToDto(Object[] data) {
        VehicleViolationReportDTO dto = new VehicleViolationReportDTO();
        dto.setDeviceId((String) data[0]);
        dto.setVehicleNumber((String) data[1]);
        dto.setVehicleType((String) data[2]);
        dto.setDealerName((String) data[3]);
        dto.setOwnerName((String) data[4]);
        dto.setAdditionalData((String) data[5]);
        dto.setTimestamp(data[6].toString());
        dto.setLatitude(data[7].toString());
        dto.setLongitude(data[8].toString());
        dto.setSpeed(data[9].toString());
        dto.setStatus((String) data[10]);
        return dto;
    }
    
    
    
    public List<ViolationSummaryDTO> getViolationSummary() {
        List<ViolationSummaryDTO> summaryList = new ArrayList<>();
        
        try {
            List<Object[]> results = violationReportRepository.getViolationSummaryLast30Days();
            
            if (!results.isEmpty() && results.get(0) != null) {
                Object[] row = results.get(0);
                
                summaryList.add(new ViolationSummaryDTO("Overspeed", 
                    row[0] != null ? ((Number) row[0]).longValue() : 0L));
                summaryList.add(new ViolationSummaryDTO("Sharp Turning", 
                    row[1] != null ? ((Number) row[1]).longValue() : 0L));
                summaryList.add(new ViolationSummaryDTO("Harsh Acceleration", 
                    row[2] != null ? ((Number) row[2]).longValue() : 0L));
                summaryList.add(new ViolationSummaryDTO("Harsh Breaking", 
                    row[3] != null ? ((Number) row[3]).longValue() : 0L));
            }
        } catch (Exception e) {
            // If there's an error, return default values
            summaryList.add(new ViolationSummaryDTO("Overspeed", 0L));
            summaryList.add(new ViolationSummaryDTO("Sharp Turning", 0L));
            summaryList.add(new ViolationSummaryDTO("Harsh Acceleration", 0L));
            summaryList.add(new ViolationSummaryDTO("Harsh Breaking", 0L));
        }
        
        return summaryList;
    }
    
    
    
    
    
    
    
    
    
    
}
