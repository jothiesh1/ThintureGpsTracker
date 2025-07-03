package com.GpsTracker.Thinture.service;


	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.dto.VehicleViolationReportDTO;
import com.GpsTracker.Thinture.repository.VehicleViolationReportRepository;

import java.sql.Timestamp;
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
}
