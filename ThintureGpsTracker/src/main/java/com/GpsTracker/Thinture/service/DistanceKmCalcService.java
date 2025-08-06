package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.DistanceKmCalcDTO;
import com.GpsTracker.Thinture.model.VehicleFuelLog;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.repository.DistanceKmCalcRepository;
import com.GpsTracker.Thinture.repository.VehicleFuelLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistanceKmCalcService {

    private static final Logger log = LoggerFactory.getLogger(DistanceKmCalcService.class);

    @Autowired
    private DistanceKmCalcRepository repo;
  
    @Autowired
    private VehicleFuelLogRepository fuelRepo; // ✅ Added for mileage calculation

    public DistanceKmCalcDTO calculate(String deviceId, String imei, LocalDate selectedDate) {
        log.info("📍 Starting KM calculation for deviceId: {}, imei: {}, date: {}", deviceId, imei, selectedDate);

        List<VehicleHistory> totalLogs = null;
        List<VehicleHistory> dayLogs = null;

        boolean isIMEI = (imei != null && !imei.isEmpty());
        boolean isDevice = (deviceId != null && !deviceId.isEmpty());

        if (!isIMEI && !isDevice) {
            throw new IllegalArgumentException("Either IMEI or Device ID must be provided");
        }

        if (isIMEI) {
            totalLogs = repo.findByImeiOrdered(imei);
            if (selectedDate != null) {
                Timestamp start = Timestamp.valueOf(selectedDate.atStartOfDay());
                Timestamp end = Timestamp.valueOf(selectedDate.plusDays(1).atStartOfDay());
                dayLogs = repo.findByImeiAndDateRange(imei, start, end);
            } else {
                dayLogs = repo.findTodayByImei(imei);
            }
        } else if (isDevice) {
            totalLogs = repo.findByDeviceIdOrdered(deviceId);
            if (selectedDate != null) {
                Timestamp start = Timestamp.valueOf(selectedDate.atStartOfDay());
                Timestamp end = Timestamp.valueOf(selectedDate.plusDays(1).atStartOfDay());
                dayLogs = repo.findByDeviceIdAndDateRange(deviceId, start, end);
            } else {
                dayLogs = repo.findTodayByDeviceId(deviceId);
            }
        }

        double totalKm = calculateDistance(totalLogs);
        double todayKm = calculateDistance(dayLogs);

        // 🔁 Mileage Calculation
        double mileage = 0.0;
        if (deviceId != null && selectedDate != null) {
            Double fuelUsed = fuelRepo.getTotalFuelFilled(deviceId, selectedDate);
            if (fuelUsed != null && fuelUsed > 0) {
                mileage = todayKm / fuelUsed;
            }
        }

        DistanceKmCalcDTO dto = new DistanceKmCalcDTO(imei, mileage);
        dto.setDeviceImei(imei);
        dto.setDeviceId(deviceId);
        dto.setTotalKm(Math.round(totalKm * 100.0) / 100.0);
        dto.setTodayKm(Math.round(todayKm * 100.0) / 100.0);
        dto.setMileage(Math.round(mileage * 100.0) / 100.0);

        log.info("✅ Total KM: {}, Today KM: {}, Mileage: {} km/l for deviceId: {}, imei: {}", totalKm, todayKm, mileage, deviceId, imei);

        return dto;
    }

    private double calculateDistance(List<VehicleHistory> logs) {
        if (logs == null || logs.size() < 2) return 0.0;

        double distance = 0.0;
        VehicleHistory prev = null;

        for (VehicleHistory vh : logs) {
            if (prev != null) {
                double segment = haversine(
                        prev.getLatitude(), prev.getLongitude(),
                        vh.getLatitude(), vh.getLongitude());
                distance += segment;
                log.debug("📍 Segment distance: {} km from {} to {}", segment, prev.getTimestamp(), vh.getTimestamp());
            }
            prev = vh;
        }
        log.debug("🔁 Total calculated distance: {} km", distance);
        return distance;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    public void saveFuelLog(VehicleFuelLog log) {
        fuelRepo.save(log);
    }
    
    public List<DistanceKmCalcDTO> getVehicleKmSummary(String deviceId, String type) {
        List<VehicleHistory> logs = repo.findByDeviceIdOrdered(deviceId);
        Map<String, List<VehicleHistory>> groupedLogs = new LinkedHashMap<>();

        for (VehicleHistory vh : logs) {
            String key;
            switch (type.toLowerCase()) {
                case "day":
                    key = vh.getTimestamp().toLocalDateTime().toLocalDate().toString();
                    break;
                case "week":
                    LocalDate date = vh.getTimestamp().toLocalDateTime().toLocalDate();
                    key = date.getYear() + "-W" + date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
                    break;
                default:
                    key = vh.getTimestamp().toLocalDateTime().getMonth().toString();
                    break;
            }

            groupedLogs.computeIfAbsent(key, k -> new ArrayList<>()).add(vh);
        }

        List<DistanceKmCalcDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<VehicleHistory>> entry : groupedLogs.entrySet()) {
            double km = calculateDistance(entry.getValue());
            result.add(new DistanceKmCalcDTO(entry.getKey(), Math.round(km * 100.0) / 100.0));
        }

        return result;
    }



}
