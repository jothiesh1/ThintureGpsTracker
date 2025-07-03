package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.DistanceKmCalcDTO;
import com.GpsTracker.Thinture.model.VehicleFuelLog;
import com.GpsTracker.Thinture.repository.VehicleFuelLogRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.service.DistanceKmCalcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/distance")
@CrossOrigin(origins = "*")
public class DistanceKmCalcController {

    private static final Logger logger = LoggerFactory.getLogger(DistanceKmCalcController.class);

    @Autowired
    private DistanceKmCalcService service;

    @Autowired
    private VehicleFuelLogRepository fuelRepo;

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/km")
    public DistanceKmCalcDTO getDistanceKm(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String imei,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        logger.info("📦 [GET] Calculating distance for deviceId={}, imei={}, date={}", deviceId, imei, date);
        return service.calculate(deviceId, imei, date);
    }

    @PostMapping("/fuel/add")
    public ResponseEntity<String> addFuelEntry(@RequestBody VehicleFuelLog log) {
        if (log.getDeviceId() == null || log.getDate() == null || log.getFuelFilled() <= 0) {
            logger.warn("❌ Invalid fuel entry detected: {}", log);
            return ResponseEntity.badRequest().body("❌ Invalid fuel entry");
        }

        fuelRepo.save(log);
        logger.info("✅ Fuel entry saved for deviceId={}, fuelFilled={}", log.getDeviceId(), log.getFuelFilled());
        return ResponseEntity.ok("✅ Fuel entry saved for device: " + log.getDeviceId());
    }

    @GetMapping("/summary")
    public List<DistanceKmCalcDTO> getVehicleKmSummary(
            @RequestParam String deviceId,
            @RequestParam(defaultValue = "month") String type) {
        logger.info("📊 Fetching KM summary for deviceId={}, type={}", deviceId, type);
        return service.getVehicleKmSummary(deviceId, type);
    }

    @GetMapping("/ids")
    public List<Map<String, String>> getVehicleIds() {
        logger.info("🚗 [GET] Fetching vehicle device IDs...");

        List<Map<String, String>> list = vehicleRepository.findAll().stream()
            .filter(v -> v.getDeviceID() != null && !v.getDeviceID().isBlank())
            .peek(v -> logger.debug("✅ Valid Vehicle: {}", v.getDeviceID()))
            .map(v -> Map.of("id", v.getDeviceID()))
            .collect(Collectors.toList());

        logger.info("✅ Total vehicle IDs returned: {}", list.size());
        return list;
    }
}
