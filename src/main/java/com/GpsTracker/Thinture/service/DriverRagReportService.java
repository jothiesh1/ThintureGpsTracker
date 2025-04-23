package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.DriverRagReportDTO;
import com.GpsTracker.Thinture.dto.DriverRagSummaryDTO;
import com.GpsTracker.Thinture.repository.DriverRagReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class DriverRagReportService {

    private static final Logger logger = LoggerFactory.getLogger(DriverRagReportService.class);
    private final DriverRagReportRepository repository;

    public DriverRagReportService(DriverRagReportRepository repository) {
        this.repository = repository;
    }

    public DriverRagSummaryDTO getDriverRagReport(Timestamp start, Timestamp end, String deviceId) {
        logger.info("🔍 Fetching Driver RAG Report for Device ID: {}, From: {}, To: {}", deviceId, start, end);

        List<Object[]> rawData = repository.getDriverRagReportRaw(start, end, deviceId);
        logger.debug("📦 Raw records fetched from DB: {}", rawData.size());

        if (rawData.isEmpty()) {
            return new DriverRagSummaryDTO();
        }

        double maxSpeed = 0.0;
        double totalDistance = 0.0;
        Double lastLat = null, lastLng = null;
        Object[] first = rawData.get(0);

        for (Object[] row : rawData) {
            Double latitude = row[7] != null ? ((Number) row[7]).doubleValue() : null;
            Double longitude = row[8] != null ? ((Number) row[8]).doubleValue() : null;
            Double speed = row[9] != null ? ((Number) row[9]).doubleValue() : null;

            if (speed != null && speed > maxSpeed) {
                maxSpeed = speed;
            }

            if (lastLat != null && lastLng != null && latitude != null && longitude != null) {
                totalDistance += haversine(lastLat, lastLng, latitude, longitude);
            }

            lastLat = latitude;
            lastLng = longitude;
        }

        // Mock data for demo purposes (replace with actual fields when available)
        double speedingTimeInSeconds = 120; // total time speeding
        int harshAccelerationCount = 5;
        int harshBrakingCount = 3;
        double seatbeltOffTimeInSeconds = 60;

        // RAG score calculation
        double speedScore = (speedingTimeInSeconds / 10.0) / totalDistance * 1000;
        double accelScore = harshAccelerationCount / totalDistance * 100;
        double decelScore = harshBrakingCount / totalDistance * 100;
        double seatbeltScore = (seatbeltOffTimeInSeconds / 10.0) / totalDistance * 1000;

        double totalScore = speedScore + accelScore + decelScore + seatbeltScore;

        DriverRagReportDTO summaryDTO = new DriverRagReportDTO(
            (String) first[0], (String) first[1], (String) first[2],
            (String) first[3], (String) first[4], String.valueOf(totalScore),
            (Timestamp) first[6],
            (Double) (first[7] != null ? ((Number) first[7]).doubleValue() : null),
            (Double) (first[8] != null ? ((Number) first[8]).doubleValue() : null),
            (Double) (first[9] != null ? ((Number) first[9]).doubleValue() : null),
            (String) first[10],
            first[11] != null ? ((Number) first[11]).longValue() : null,
            (String) first[12],
            maxSpeed,
            totalDistance
        );

        List<DriverRagReportDTO> list = new ArrayList<>();
        list.add(summaryDTO);

        DriverRagSummaryDTO summary = new DriverRagSummaryDTO();
        summary.setReportList(list);
        summary.setMaxSpeed(maxSpeed);
        summary.setTotalDistance(totalDistance);

        logger.info("✅ Final Merged Record Sent. Max Speed: {}, Total KM: {}, RAG Score: {}", maxSpeed, totalDistance, totalScore);
        return summary;
    }
    
    public DriverRagSummaryDTO getDriverRagReportForAll(Timestamp start, Timestamp end) {
        List<String> allDeviceIds = getAllDeviceIds();
        List<DriverRagReportDTO> allReports = new ArrayList<>();

        double maxSpeed = 0.0;
        double totalDistance = 0.0;

        for (String deviceId : allDeviceIds) {
            DriverRagSummaryDTO summary = getDriverRagReport(start, end, deviceId);
            if (!summary.getReportList().isEmpty()) {
                allReports.addAll(summary.getReportList());
                maxSpeed = Math.max(maxSpeed, summary.getMaxSpeed());
                totalDistance += summary.getTotalDistance();
            }
        }

        DriverRagSummaryDTO merged = new DriverRagSummaryDTO();
        merged.setReportList(allReports);
        merged.setMaxSpeed(maxSpeed);
        merged.setTotalDistance(totalDistance);
        return merged;
    }


    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<String> getAllDeviceIds() {
        return repository.findAllDeviceIds();
    }
}
