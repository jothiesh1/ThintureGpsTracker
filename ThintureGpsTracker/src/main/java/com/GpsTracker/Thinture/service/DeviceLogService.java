package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.DeviceLogDTO;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.repository.DeviceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceLogService {

    @Autowired
    private DeviceLogRepository logRepository;

    public List<DeviceLogDTO> fetchLogsByDate(String deviceID, LocalDate date) {
        Timestamp start = Timestamp.valueOf(date.atStartOfDay());
        Timestamp end = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
        List<VehicleHistory> logs = logRepository.findHistoryByDeviceIDAndDateRange(deviceID, start, end);
        
        return logs.stream().map(log -> {
            DeviceLogDTO dto = new DeviceLogDTO();
            if (log.getVehicle() != null) {
                dto.setDeviceID(log.getVehicle().getDeviceID());
                dto.setImei(log.getVehicle().getImei());
            }
            dto.setTimestamp(log.getTimestamp().toString());
            dto.setSpeed(String.valueOf(log.getSpeed()));
            dto.setLatitude(log.getLatitude());
            dto.setLongitude(log.getLongitude());
            dto.setIgnition(log.getIgnition());
            dto.setVehicleStatus(log.getVehicleStatus());
            dto.setAdditionalData(log.getAdditionalData());
            return dto;
        }).collect(Collectors.toList());
    }

}
