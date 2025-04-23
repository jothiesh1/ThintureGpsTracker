package com.GpsTracker.Thinture.scheduler;

import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleDailyKm;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.repository.DistanceKmCalcRepository;
import com.GpsTracker.Thinture.repository.VehicleDailyKmRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class VehicleHalfHourScheduler {

    private static final Logger log = LoggerFactory.getLogger(VehicleHalfHourScheduler.class);

    @Autowired private VehicleRepository vehicleRepo;
    @Autowired private DistanceKmCalcRepository historyRepo;
    @Autowired private VehicleDailyKmRepository dailyKmRepo;

    // 🔁 Runs every 30 minutes
    @Scheduled(cron = "0 0/30 * * * *")
    public void calculateHalfHourKms() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDate today = now.toLocalDate();
        String slotStr = formatTimeSlot(start, now);

        log.info("⏱ Scheduler triggered - Slot: {}", slotStr);

        for (Vehicle vehicle : vehicleRepo.findAll()) {
            String deviceId = vehicle.getDeviceID();
            String imei = vehicle.getImei();

            Timestamp startTs = Timestamp.valueOf(start);
            Timestamp endTs = Timestamp.valueOf(now);

            List<VehicleHistory> logs = historyRepo.findByDeviceIdAndDateRange(deviceId, startTs, endTs);
            double km = calculateDistance(logs);

            if (!dailyKmRepo.existsByDeviceIdAndDateAndTimeSlot(deviceId, today, slotStr)) {
                VehicleDailyKm record = new VehicleDailyKm();
                record.setDeviceId(deviceId);
                record.setImei(imei);
                record.setDate(today);
                record.setTimeSlot(slotStr);
                record.setKmTravelled(Math.round(km * 100.0) / 100.0);

                dailyKmRepo.save(record);
                log.info("✅ Stored {} KM for {} at slot {}", km, deviceId, slotStr);
            } else {
                log.warn("⚠️ Duplicate skipped for {} at slot {}", deviceId, slotStr);
            }
        }
    }

    private double calculateDistance(List<VehicleHistory> logs) {
        if (logs == null || logs.size() < 2) return 0.0;

        double distance = 0.0;
        VehicleHistory prev = null;

        for (VehicleHistory vh : logs) {
            if (prev != null) {
                double segment = haversine(prev.getLatitude(), prev.getLongitude(),
                        vh.getLatitude(), vh.getLongitude());
                distance += segment;
                log.debug("📍 Segment: {} km from {} to {}", segment, prev.getTimestamp(), vh.getTimestamp());
            }
            prev = vh;
        }
        log.debug("📦 Total distance calculated: {} km", distance);
        return distance;
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

    private String formatTimeSlot(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        return fmt.format(start) + "–" + fmt.format(end);
    }
}